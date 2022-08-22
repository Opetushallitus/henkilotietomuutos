package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.dto.SlackMessageDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ObjectMapper.class)
public class SlackClientImplTest {

    @InjectMocks
    SlackClientImpl slackClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(this.slackClient, "objectMapper", this.objectMapper);
    }

    @Test
    public void slackMessageIsValidFormat() throws Exception {
        String error_topic = "test error topic";
        String error_message = "test error message";
        String expected_error_message = "test error topic: ```test error message```";
        SlackMessageDto messageDto = objectMapper.readValue(slackClient.constructSlackMessage(error_topic, error_message), SlackMessageDto.class);
        assertEquals(messageDto.getClass(), SlackMessageDto.class);
        assertNotNull(messageDto.getText());
        assertNull(messageDto.getBlocks().get(1).getText().getEmoji());
        assertEquals(messageDto.getBlocks().get(1).getText().getText(), expected_error_message);

    }
}
