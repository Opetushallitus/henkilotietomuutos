package fi.oph.henkilotietomuutospalvelu;

import fi.oph.henkilotietomuutospalvelu.configurations.H2Configuration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Add @Transactional to test class if you wish db to rollback after each test
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestApplication.class, H2Configuration.class})
@ActiveProfiles("integrationtest")
public @interface IntegrationTest {

}
