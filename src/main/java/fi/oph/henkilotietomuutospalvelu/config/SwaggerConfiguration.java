package fi.oph.henkilotietomuutospalvelu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true, havingValue = "true")
public class SwaggerConfiguration {

}
