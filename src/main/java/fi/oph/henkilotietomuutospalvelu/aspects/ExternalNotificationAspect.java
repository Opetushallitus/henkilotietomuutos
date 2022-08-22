package fi.oph.henkilotietomuutospalvelu.aspects;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Huom. ei thread turvallinen.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ExternalNotificationAspect {

    private final NotificationService notificationService;
    private final ViestintaProperties viestintaProperties;
    private LocalDateTime lastNotificationSent;

    @AfterThrowing(value = "@annotation(notifyOnError)", throwing = "exception")
    public void notifyOnException(JoinPoint joinPoint, NotifyOnError notifyOnError, Exception exception) {
        String process = notifyOnError.value().getValue();
        String topic = String.format("Virhe henkilötietomuutospalvelun prosessissa: %s", process);
        String errorMessage = String.format("%s: %s", LocalDateTime.now(), sanitize(exception.toString()));

        if (lastNotificationSent == null || MINUTES.between(lastNotificationSent, LocalDateTime.now()) >= this.viestintaProperties.getMaxNotificationIntervalInMinutes()) {
            if (StringUtils.hasLength(this.viestintaProperties.getDefaultReceiverEmail())) {
                this.notificationService.sendEmailNotification(topic, errorMessage);
            }
            if (StringUtils.hasLength(this.viestintaProperties.getSlackUrl())) {
                this.notificationService.sendSlackNotification(topic, errorMessage);
            }
            this.lastNotificationSent = LocalDateTime.now();
        }
    }

    protected String sanitize(String message) {
        return message.replaceAll("\\b(\\d{6}[-+A])\\d{3}\\w(\\W|\\b)", "$1****$2");
    }
}
