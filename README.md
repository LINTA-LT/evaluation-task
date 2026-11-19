# PDB

PDB is the public search application for intellectual property rights: trademarks, designs, patents, disputes and Nice classification.

- Java 8, Spring Boot 2.2, Thymeleaf pages
- Elasticsearch 7.10.0
- Mysql 8
- trademarks, designs, disputes and Nice classification are searched in Elasticsearch, which is filled from MySQL by the Logstash pipelines in `src/main/resources/logstash-pipelines`
- patents are read from MySQL directly through JPA
- the application context path is `/pdb`

### Prerequisites

File system locations configured by application:

Local machine/container
* logging.file.name: ./pdb.log

Host system shared resources
<pre> 
tmdsview.binarydata-dir.tm: /home/myuser/cti/images/tmview
patent.images-dir:          /home/myuser/cti/images/patents
opendata.root-dir:          /home/myuser/open-data
</pre>

Settings per environment are taken from the Spring profile. The profile `local` is provided in `src/main/resources/application-local.yml`, Logstash pipeline values for it are in `src/main/resources/logstash-pipeline-filters/local-filter-values.properties`. All values there are placeholders (`localhost`, user `pdb`, password `pdb`).

### Build

<blockquote>

./mvnw clean package  
</blockquote>

This builds `target/pdb.jar` and, through the docker-maven-plugin, the Docker image `pdb` (Docker is needed on the machine). `mvn install` also tries to push the image, so skip it or configure a registry in `pom.xml`.

### Docker run

Stop and remove existing container if needed

<blockquote>

docker stop pdb  
docker rm pdb  
</blockquote>

Start container with inline customizations
<blockquote>

docker run -d  
--name pdb  
-p **container-port**:**application-port**  
-v **vm-home-dir**:/home/myuser  
-e "SPRING_PROFILES_ACTIVE=**profile**"  
pdb
</blockquote>

where values should be specified for following placeholders  
<blockquote>

**container-port** docker container port for incoming connection  
**application-port** application port inside a container   
**vm-home-dir** root dir for multiple file storage purposes (cti related files, patents, open-data) on host machine. In the examples it is /home/myuser. Same name given for docker container mount.      
**profile** spring profile 
</blockquote>

For example:  
docker run -d --name pdb -p 8060:8080 -v /home/myuser:/home/myuser -e "SPRING_PROFILES_ACTIVE=local" pdb
