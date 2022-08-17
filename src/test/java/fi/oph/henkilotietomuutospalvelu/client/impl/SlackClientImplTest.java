package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.dto.SlackMessageDto;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.VtjDataRepository;
import fi.oph.henkilotietomuutospalvelu.service.FileService;
import fi.oph.henkilotietomuutospalvelu.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ObjectMapper.class)
public class SlackClientImplTest {

    @InjectMocks
    SlackClientImpl slackClient;

    @Test
    public void SlackMessageIsValidFormat() {

        Gson gson = new Gson();
        String error_topic = "test error topic";
        String error_message = "test error message";
        String expected_error_message = "test error topic: ```test error message```";

        assertEquals(gson.fromJson(slackClient.constructSlackMessage(error_topic, error_message), SlackMessageDto.class).getClass(), SlackMessageDto.class);
        SlackMessageDto messageDto = gson.fromJson(slackClient.constructSlackMessage(error_topic, error_message), SlackMessageDto.class);
        assertNotNull(messageDto.getText());
        assertNull(messageDto.getBlocks().get(1).getText().getEmoji());
        assertEquals(messageDto.getBlocks().get(1).getText().getText(), expected_error_message);

    }
}
