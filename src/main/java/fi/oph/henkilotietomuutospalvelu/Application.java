package fi.oph.henkilotietomuutospalvelu;

import com.google.common.base.Predicates;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket excludeErrorController(){
        return new Docket(DocumentationType.SWAGGER_2).select()
            // exclude error controller from swagger
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
            .build();
    }

}
