package fi.oph.henkilotietomuutospalvelu.client;

/**
 * Slack integration client
 */
public interface SlackClient {
    /**
     * Sends a message to Slack
     * @param topic message topic
     * @param message error message details
     */
    String constructSlackMessage(String topic, String message);

    /**
     * Slack message formatted
     * @param slackMessage Slack message json representation
     */
    void sendToSlack(String slackMessage);
}
