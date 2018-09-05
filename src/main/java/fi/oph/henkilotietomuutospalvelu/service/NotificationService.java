package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;

/**
 * Palvelu notifikaatioiden lähettämiseen
 */
public interface NotificationService {
    /**
     * Lähettää sähköpostinotifikaation
     * @param topic Sähköpostiviestin otsikko
     * @param message Sähköpostiviestin tekstin sisältö
     */
    void sendEmailNotification(String topic, String message);

    /**
     * Lähettää flowdoc notifikaation
     * @param topic Notifikaatioviestin tekstin otsikko
     * @param message Notifikaatioviestin tekstin sisältö
     * @param notifyOnError Notifikaation tyyppi
     */
    void sendFlowdocNotification(String topic, String message, NotifyOnError notifyOnError);
}
