package fi.oph.henkilotietomuutospalvelu.config.scheduling;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Ajastuksen aktivointi.
 *
 * @see ScheduledTasks ajastusten konfigurointi
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true")
public class SchedulingConfiguration {

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        // Set to 2 so single long task won't block everything
        return Executors.newScheduledThreadPool(1);
    }
}
