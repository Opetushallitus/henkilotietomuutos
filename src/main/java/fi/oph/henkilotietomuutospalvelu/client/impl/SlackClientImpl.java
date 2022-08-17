package fi.oph.henkilotietomuutospalvelu.client.impl;
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
import com.google.gson.*;

import javax.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.http.HttpStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackClientImpl implements SlackClient {
    private final ViestintaProperties viestintaProperties;

    private OphHttpClient ophHttpClient;

    private final Gson gson = new Gson();

    private static final String MESSAGE_HEADER_TYPE = "header";
    private static final String MESSAGE_SECTION_TYPE = "section";
    private static final String MESSAGE_HEADER_TEXT = ":ghost: Error in Henkilötietomuutospalvelu :ghost:";
    private static final String MESSAGE_HEADER_TEXT_TYPE = "plain_text";
    private static final String MESSAGE_SECTION_TEXT_TYPE = "mrkdwn";


    @PostConstruct
    public void setup() {
        this.ophHttpClient = new OphHttpClient.Builder(ConfigEnums.CALLER_ID.value())
                .build();
    }

    @Override
    public String constructSlackMessage(String topic, String message) {
        String content = String.format("%s: ```%s```", topic, message);
        SlackMessageDto slackMsg = new SlackMessageDto();
        slackMsg.setText(MESSAGE_HEADER_TEXT);
        SlackMessageDto.MessageBlock msgHeader = new SlackMessageDto.MessageBlock();
        msgHeader.setType(MESSAGE_HEADER_TYPE);
        msgHeader.setText(new SlackMessageDto.MessageBlock.Text(MESSAGE_HEADER_TEXT_TYPE, MESSAGE_HEADER_TEXT, true));
        SlackMessageDto.MessageBlock msgSection = new SlackMessageDto.MessageBlock();
        msgSection.setType(MESSAGE_SECTION_TYPE);
        msgSection.setText(new SlackMessageDto.MessageBlock.Text(MESSAGE_SECTION_TEXT_TYPE, content));
        slackMsg.setBlocks(Arrays.asList(msgHeader, msgSection));
        return gson.toJson(slackMsg);
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
