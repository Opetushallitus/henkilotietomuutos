package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.SlackClient;
import fi.oph.henkilotietomuutospalvelu.client.RyhmasahkopostiClient;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.service.NotificationService;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ViestintaProperties viestintaProperties;

    private final RyhmasahkopostiClient ryhmasahkopostiClient;

    private final SlackClient slackClient;

    private static final String CALLING_PROCESS = "henkilotietomuutos";

    @Override
    public void sendEmailNotification(String topic, String message) {
        this.sendEmail(viestintaProperties.getDefaultReceiverEmail(), topic, message);
    }

    private void sendEmail(String receiver, String topic, String message) {
        String defaultReplyEmail = this.viestintaProperties.getDefaultReplyEmail();
        EmailData emailData = new EmailData();

        EmailRecipient emailRecipient = new EmailRecipient();
        emailRecipient.setEmail(receiver);
        emailData.setRecipient(Collections.singletonList(emailRecipient));

        EmailMessage emailMessage = new EmailMessage(CALLING_PROCESS, defaultReplyEmail, defaultReplyEmail, topic, message);
        emailData.setEmail(emailMessage);

        try {
            this.ryhmasahkopostiClient.sendRyhmasahkoposti(emailData);
        } catch (RuntimeException e) {
            log.warn("Could not send email notification", e);
        }
    }

    @Override
    public void sendSlackNotification(String topic, String message) {
        String slackMessage = this.slackClient.constructSlackMessage(topic, message);
        this.slackClient.sendToSlack(slackMessage);
    }
}
