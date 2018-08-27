package fi.oph.henkilotietomuutospalvelu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile("integrationtest")
@SpringBootApplication
public class TestApplication {

    @Configuration
    @EnableJpaRepositories(basePackages = "fi.oph.henkilotietomuutospalvelu.repository")
    @PropertySource("application.yml")
    @EnableTransactionManagement
    public class H2JpaConfig {

    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
