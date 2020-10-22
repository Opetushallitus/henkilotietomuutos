package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Turvakielto;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TurvakieltoParserTest {

    @Test
    public void parsesTurvakielto() {
        String tietoryhma = "015120201022";
        Turvakielto turvakielto = TurvakieltoParser.parseTurvakielto(tietoryhma);
        assertEquals(LocalDate.of(2020, 10, 22), turvakielto.getEndDate());
    }

    @Test
    public void parsesIndefiniteTurvakielto() {
        String tietoryhma = "015199990000";
        Turvakielto turvakielto = TurvakieltoParser.parseTurvakielto(tietoryhma);
        assertNull(turvakielto.getEndDate());
    }

    @Test
    public void serializesTurvakielto() {
        String tietoryhma = "015120201022";
        Turvakielto turvakielto = TurvakieltoParser.parseTurvakielto(tietoryhma);
        assertEquals(tietoryhma, TurvakieltoParser.serializeTurvakielto(turvakielto));
    }

    @Test
    public void serializesIndefiniteTurvakielto() {
        String tietoryhma = "015199990000";
        Turvakielto turvakielto = TurvakieltoParser.parseTurvakielto(tietoryhma);
        assertEquals(tietoryhma, TurvakieltoParser.serializeTurvakielto(turvakielto));
    }
}
