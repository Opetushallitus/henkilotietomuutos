package fi.oph.henkilotietomuutospalvelu.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "clients.amazon")
public class AWSProperties {

    @Getter(AccessLevel.NONE)
    private boolean enabled;
    private String region;
    private String bucket;

    public Boolean isS3InUse() {
        return enabled;
    }

}
