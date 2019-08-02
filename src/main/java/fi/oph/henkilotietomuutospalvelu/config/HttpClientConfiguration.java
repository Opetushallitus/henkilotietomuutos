package fi.oph.henkilotietomuutospalvelu.config;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.apache.ApacheOphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

    @Bean
    public OphHttpClient httpClient(OphProperties properties) {
        return ApacheOphHttpClient.createDefaultOphClient(ConfigEnums.CALLER_ID.value(), properties, 10000, 60);
    }

}
