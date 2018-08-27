package fi.oph.henkilotietomuutospalvelu.client;

import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;

public interface RyhmasahkopostiClient {
    void sendRyhmasahkoposti(EmailData emailData);
}
