package fi.oph.henkilotietomuutospalvelu.client;

import fi.oph.henkilotietomuutospalvelu.dto.FlowdocMessagesApiDto;

/**
 * Flowdoc integraation clientti
 */
public interface FlowdocClient {
    /**
     * Lähettää message-apiin message-tyyppisen eventin
     * @param content Notifikaation tekstisisältö
     * @param tag Halutut tagit jotka näkyvät flowdockin viestissä
     */
    void sendMessagesApiMessageEvent(String content, String tag);

    /**
     * Flowdocking message-apiin viesti
     * @param flowdocMessagesApiDto Viestin sisältö
     */
    void sendToMessagesApi(FlowdocMessagesApiDto flowdocMessagesApiDto);
}
