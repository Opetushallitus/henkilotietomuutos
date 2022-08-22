package fi.oph.henkilotietomuutospalvelu.service;

public interface NotificationService {
    /**
     * Sends an email notification
     * @param topic Email topic
     * @param message Email message
     */
    void sendEmailNotification(String topic, String message);

    /**
     * Sends a notification to Slack
     * @param topic Notification topic
     * @param message Notification message
     */
    void sendSlackNotification(String topic, String message);
}
