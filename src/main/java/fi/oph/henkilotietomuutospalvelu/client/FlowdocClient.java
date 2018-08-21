package fi.oph.henkilotietomuutospalvelu.client;

import fi.oph.henkilotietomuutospalvelu.dto.FlowdocMessagesApiDto;

public interface FlowdocClient {
    void sendMessagesApiMessageEvent(String content, String tag);

    void sendToMessagesApi(FlowdocMessagesApiDto flowdocMessagesApiDto);
}
