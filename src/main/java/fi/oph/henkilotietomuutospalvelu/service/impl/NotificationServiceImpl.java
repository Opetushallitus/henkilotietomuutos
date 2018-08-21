package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;
import fi.oph.henkilotietomuutospalvelu.client.FlowdocClient;
import fi.oph.henkilotietomuutospalvelu.client.RyhmasahkopostiClient;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.service.NotificationService;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ViestintaProperties viestintaProperties;

    private final RyhmasahkopostiClient ryhmasahkopostiClient;

    private final FlowdocClient flowdocClient;

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

        this.ryhmasahkopostiClient.sendRyhmasahkoposti(emailData);
    }

    @Override
    public void sendFlowdocNotification(String topic, String message, NotifyOnError notifyOnError) {
        String content = String.format("%s: %s", topic, message);
        this.flowdocClient.sendMessagesApiMessageEvent(content, notifyOnError.value().name());
    }
}
