package fi.oph.henkilotietomuutospalvelu.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cas", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class CasProperties {
    private String service;
    private Boolean sendRenew;
    private String key;
    private String url;
    private String fallbackUserDetailsProviderUrl;
}
