package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.client.FlowdocClient;
import fi.oph.henkilotietomuutospalvelu.config.UrlConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.dto.FlowdocMessagesApiDto;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;

import java.util.Optional;

import static org.apache.http.HttpStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlowdocClientImpl implements FlowdocClient {
    private final ObjectMapper objectMapper;
    private final ViestintaProperties viestintaProperties;
    private final UrlConfiguration urlConfiguration;

    private OphHttpClient ophHttpClient;

    private static final String MESSAGE_EVENT_TYPE = "message";

    @PostConstruct
    public void setup() {
        this.ophHttpClient = new OphHttpClient.Builder("HenkilotietomuutosPalvelu")
                .build();
    }

    @Override
    public void sendMessagesApiMessageEvent(String content, String tag) {
        FlowdocMessagesApiDto flowdocMessagesApiDto = new FlowdocMessagesApiDto();
        flowdocMessagesApiDto.setEvent(MESSAGE_EVENT_TYPE);
        flowdocMessagesApiDto.setTags(tag);
        flowdocMessagesApiDto.setFlow_token(viestintaProperties.getFlowToken());
        flowdocMessagesApiDto.setContent(content);
        this.sendToMessagesApi(flowdocMessagesApiDto);
    }

    @Override
    public void sendToMessagesApi(FlowdocMessagesApiDto flowdocMessagesApiDto) {
        String url = this.urlConfiguration.url("flowdoc-api.messages");
        String content;
        try {
            content = this.objectMapper.writeValueAsString(flowdocMessagesApiDto);
        } catch (JsonProcessingException jpe) {
            throw new RestClientException("Json processing failure", jpe);
        }
        OphHttpRequest ophHttpRequest = OphHttpRequest.Builder
                .post(url)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                .setEntity(new OphHttpEntity.Builder()
                        .content(content)
                        .contentType(ContentType.APPLICATION_JSON)
                        .build())
                .build();
        this.ophHttpClient.execute(ophHttpRequest)
                .handleErrorStatus(SC_GONE).with(errorMessage -> {
                    log.warn("Could not send flowdoc notification with error {}", errorMessage);
                    return Optional.empty();
                })
                .expectedStatus(SC_OK, SC_ACCEPTED)
                .ignoreResponse();
    }
}
