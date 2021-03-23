package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Turvakielto;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TurvakieltoParserTest {

    TurvakieltoParser parser = new TurvakieltoParser();

    @Test
    public void parsesTurvakielto() {
        String tietoryhma = "015120201022";
        Turvakielto turvakielto = parser.parse(tietoryhma);
        assertEquals(LocalDate.of(2020, 10, 22), turvakielto.getEndDate());
    }

    @Test
    public void parsesIndefiniteTurvakielto() {
        String tietoryhma = "015199990000";
        Turvakielto turvakielto = parser.parse(tietoryhma);
        assertNull(turvakielto.getEndDate());
    }

    @Test
    public void serializesTurvakielto() {
        String tietoryhma = "015120201022";
        Turvakielto turvakielto = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(turvakielto));
    }

    @Test
    public void serializesIndefiniteTurvakielto() {
        String tietoryhma = "015199990000";
        Turvakielto turvakielto = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(turvakielto));
    }
}
