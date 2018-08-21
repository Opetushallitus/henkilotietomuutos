package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;

public interface NotificationService {
    void sendEmailNotification(String topic, String message);

    void sendFlowdocNotification(String topic, String message, NotifyOnError notifyOnError);
}
