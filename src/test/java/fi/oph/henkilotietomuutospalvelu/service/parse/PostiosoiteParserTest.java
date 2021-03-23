package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Postiosoite;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PostiosoiteParserTest {

    PostiosoiteParser parser = new PostiosoiteParser();

    @Test
    public void parsesPostiosoite() {
        String tietoryhma = "1031Humikkalanrinne 1                                 Humikkalabrinken 1                                009402020010100000000";
        Postiosoite osoite = parser.parse(tietoryhma);
        assertEquals("Humikkalanrinne 1", osoite.getPostiosoite());
        assertEquals("Humikkalabrinken 1", osoite.getPostiosoiteSv());
        assertEquals(LocalDate.of(2020, 1, 1), osoite.getStartDate());
        assertNull(osoite.getEndDate());
    }

    @Test
    public void serializesPostiosoite() {
        String tietoryhma = "1031Humikkalanrinne 1                                 Humikkalabrinken 1                                009402020010100000000";
        Postiosoite osoite = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(osoite));
    }
}
