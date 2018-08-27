package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.RyhmasahkopostiClient;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.service.EmailService;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final ViestintaProperties viestintaProperties;

    private final RyhmasahkopostiClient ryhmasahkopostiClient;

    private static final String CALLING_PROCESS = "henkilotietomuutos";

    @Override
    public void sendEmail(String topic, String message) {
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
}
