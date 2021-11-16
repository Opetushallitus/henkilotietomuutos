package fi.oph.henkilotietomuutospalvelu.aspects;

import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ExternalNotificationAspectTest {

    private final String desc;
    private final String input;
    private final String expected;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ViestintaProperties viestintaProperties;
    private final ExternalNotificationAspect externalNotificationAspect;

    public ExternalNotificationAspectTest(String desc, String input, String expected) throws Exception {
        this.desc = desc;
        this.input = input;
        this.expected = expected;
        externalNotificationAspect = new ExternalNotificationAspect(notificationService, viestintaProperties);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> parameters() {
        return Arrays.asList(
                new String[]{"Passthrough when no match", "foo", "foo"},
                new String[]{"Detects hetu when separator is '+'", "123456+7890", "123456+****"},
                new String[]{"Detects hetu when separator is '-'", "123456-7890", "123456-****"},
                new String[]{"Detects hetu when separator is 'A'", "123456A7890", "123456A****"},
                new String[]{"Multiple matches (non-whitespace separator)", "123456-7890-123456-7890", "123456-****-123456-****"},
                new String[]{"Multiple matches (whitespace separator)", "123456+7890 123456-7890", "123456+**** 123456-****"},
                new String[]{"Rest of the line remains as is", "foo 123456+7890 bar", "foo 123456+**** bar"},
                new String[]{"Not a hetu (birth time)", "1123456+7890", "1123456+7890"},
                new String[]{"Not a hetu (suffix)", "123456+78900", "123456+78900"}
        );
    }

    @Test
    public void sanitize() {
        assertEquals(expected, externalNotificationAspect.sanitize(input));
    }
}
