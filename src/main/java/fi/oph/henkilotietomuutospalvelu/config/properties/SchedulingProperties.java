package fi.oph.henkilotietomuutospalvelu.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "scheduling")
public class SchedulingProperties {
    private boolean enabled;
    private final Enable enable = new Enable();
    private final FixedDelayInMillis fixedDelayInMillis = new FixedDelayInMillis();
    private final Cron cron = new Cron();

    @Getter
    @Setter
    public static class Enable {
        private Boolean downloading = true;
        private Boolean importing = true;
        private Boolean handling = true;
        private Boolean hetuUpdate = true;
    }

    @Getter
    @Setter
    public static class FixedDelayInMillis {
        private String handling = "1000000";
        private String downloading = "1000000";
    }

    @Getter
    @Setter
    public static class Cron {
        // Default on scheduled task
        private String hetuUpdate;
    }
}
