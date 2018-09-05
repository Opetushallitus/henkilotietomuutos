package fi.oph.henkilotietomuutospalvelu.config;

import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class UrlConfiguration extends OphProperties {

    @Autowired
    public UrlConfiguration(Environment environment) {
        addFiles("/henkilotietomuutos-service-oph.properties");
        addOverride("host-cas", environment.getRequiredProperty("host.host-cas"));
        addOverride("host-virkailija", environment.getRequiredProperty("host.host-virkailija"));
        addOverride("host-flowdoc", environment.getRequiredProperty("host.host-flowdoc"));
    }
}
