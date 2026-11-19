package lt.gov.vpb.pdb.repository.elastic;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.UnknownHostException;

import static java.net.InetAddress.getByName;

@Configuration
public class ElasticSearchServiceConfig {

    @Value("${elasticsearch.host}")
    String esHost;

    @Value("${elasticsearch.port}")
    int esPort;

    @Value("${elasticsearch.cluster-name}")
    String esClusterName;

    @Bean
    public TransportClient client() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", esClusterName).build();
        TransportAddress transportAddress = new TransportAddress(getByName(esHost), esPort);
        /**
         * NOSONAR comment added due to false-positive BUG report, that the AutoCloseable client should be used with
         * try-with-resources block. This causes the client to close as soon as the client bean is created.
         * However, the singleton instance per application is recommended by Elastic and Spring.
         */
        return new PreBuiltTransportClient(settings).addTransportAddress(transportAddress); // NOSONAR
    }

}
