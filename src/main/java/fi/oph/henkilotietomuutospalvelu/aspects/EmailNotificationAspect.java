package fi.oph.henkilotietomuutospalvelu.aspects;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.service.EmailService;
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

    private final EmailService emailService;
    private final ViestintaProperties viestintaProperties;

    @AfterThrowing(value = "@annotation(notifyOnError)", throwing = "exception")
    public void notifyOnException(JoinPoint joinPoint, NotifyOnError notifyOnError, Exception exception) {
        if (StringUtils.hasLength(this.viestintaProperties.getDefaultReceiverEmail())) {
            String process = notifyOnError.value().getValue();
            String topic = String.format("Virhe henkilötietomuutospalvelun prosessissa: %s", process);
            String errorMessage = String.format("%s: %s", LocalDateTime.now(), exception);

            this.emailService.sendEmail(topic, errorMessage);
        }
    }
}
