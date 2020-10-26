package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kutsumanimi;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class KutsumanimiParserTest {

    @Test
    public void parsesKutsumanimi() {
        String tietoryhma = "4231Kosovon Härkä                                                                                       181976111000000000 ";
        Kutsumanimi nimi = KutsumanimiParser.parseKutsumanimi(tietoryhma);
        assertEquals("Kosovon Härkä", nimi.getName());
        assertEquals("18", nimi.getType());
        assertEquals(LocalDate.of(1976, 11, 10), nimi.getStartDate());
        assertNull(nimi.getEndDate());
        assertFalse(nimi.getNonStandardCharacters());
    }

    @Test
    public void serializesKutsumanimi() {
        String tietoryhma = "4231Kosovon Härkä                                                                                       181976111000000000 ";
        Kutsumanimi nimi = KutsumanimiParser.parseKutsumanimi(tietoryhma);
        assertEquals(tietoryhma, KutsumanimiParser.serializeKutsumanimi(nimi));
    }
}
