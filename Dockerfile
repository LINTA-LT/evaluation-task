FROM eclipse-temurin:8-focal

ENV TZ=Europe/Vilnius
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get update -y && apt-get install graphicsmagick -y

ARG NAME
ADD $NAME app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
