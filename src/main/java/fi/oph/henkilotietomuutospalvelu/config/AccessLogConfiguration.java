package fi.oph.henkilotietomuutospalvelu.config;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessLogConfiguration {

    // Access log file configuration. "logback.access" is provided as launch parameter.
    @Bean
    @ConditionalOnProperty(name = "logback.access")
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                ((TomcatEmbeddedServletContainerFactory) container)
                        .addContextCustomizers((TomcatContextCustomizer) context -> {
                            LogbackValve logbackValve = new LogbackValve();
                            logbackValve.setFilename("logback-access.xml");
                            context.getPipeline().addValve(logbackValve);
                        });
            }
        };
    }
}
