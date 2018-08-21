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

@Aspect
@Component
@RequiredArgsConstructor
public class EmailNotificationAspect {

    private final NotificationService notificationService;
    private final ViestintaProperties viestintaProperties;

    @AfterThrowing(value = "@annotation(notifyOnError)", throwing = "exception")
    public void notifyOnException(JoinPoint joinPoint, NotifyOnError notifyOnError, Exception exception) {
        String process = notifyOnError.value().getValue();
        String topic = String.format("Virhe henkilötietomuutospalvelun prosessissa: %s", process);
        String errorMessage = String.format("%s: %s", LocalDateTime.now(), exception);

        if (StringUtils.hasLength(this.viestintaProperties.getDefaultReceiverEmail())) {
            this.notificationService.sendEmailNotification(topic, errorMessage);
        }
        if (StringUtils.hasLength(this.viestintaProperties.getFlowToken())) {
            this.notificationService.sendFlowdocNotification(topic, errorMessage, notifyOnError);
        }
    }
}
