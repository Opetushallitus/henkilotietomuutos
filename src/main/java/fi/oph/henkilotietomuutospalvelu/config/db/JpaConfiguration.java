package fi.oph.henkilotietomuutospalvelu.config.db;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"fi.oph.henkilotietomuutospalvelu.repository"})
@EntityScan({"fi.oph.henkilotietomuutospalvelu.model"})
@EnableTransactionManagement
public class JpaConfiguration {
}
