package fi.oph.henkilotietomuutospalvelu.client.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.client.SlackClient;
import fi.oph.henkilotietomuutospalvelu.config.ConfigEnums;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.dto.SlackMessageDto;
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

import java.util.Arrays;
import java.util.Optional;

import static org.apache.http.HttpStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackClientImpl implements SlackClient {
    private final ViestintaProperties viestintaProperties;

    private OphHttpClient ophHttpClient;
    private final ObjectMapper objectMapper;

    private static final String MESSAGE_HEADER_TEXT = ":ghost: Error in Henkilötietomuutospalvelu :ghost:";


    @PostConstruct
    public void setup() {
        this.ophHttpClient = new OphHttpClient.Builder(ConfigEnums.CALLER_ID.value())
                .build();
    }

    @Override
    public String constructSlackMessage(String topic, String message) {
        String content = String.format("%s: ```%s```", topic, message);
        SlackMessageDto slackMsgDto = new SlackMessageDto();
        slackMsgDto.setText(MESSAGE_HEADER_TEXT);
        SlackMessageDto.MessageBlock msgHeader = new SlackMessageDto.MessageBlock();
        msgHeader.setType(SlackMessageDto.MessageBlock.Type.header);
        msgHeader.setText(new SlackMessageDto.MessageBlock.Text(SlackMessageDto.MessageBlock.Text.Type.plain_text, MESSAGE_HEADER_TEXT, true));
        SlackMessageDto.MessageBlock msgSection = new SlackMessageDto.MessageBlock();
        msgSection.setType(SlackMessageDto.MessageBlock.Type.section);
        msgSection.setText(new SlackMessageDto.MessageBlock.Text(SlackMessageDto.MessageBlock.Text.Type.mrkdwn, content));
        slackMsgDto.setBlocks(Arrays.asList(msgHeader, msgSection));
        String slackMsgJson;
        try {
            slackMsgJson = this.objectMapper.writeValueAsString(slackMsgDto);
        } catch (JsonProcessingException jpe) {
            throw new RestClientException("Json processing failure", jpe);
        }
        return slackMsgJson;
    }

    @Override
    public void sendToSlack(String content) {
        String url = viestintaProperties.getSlackUrl();
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
                    log.warn("Could not send Slack notification with error {}", errorMessage);
                    return Optional.empty();
                })
                .expectedStatus(SC_OK, SC_ACCEPTED)
                .ignoreResponse();
    }
}
