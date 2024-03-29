package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kansalaisuus;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class KansalaisuusParserTest {
    
    private final KansalaisuusParser parser = new KansalaisuusParser();

    @Test
    public void parsesKansalaisuus() {
        String value = "0071FIN12020102100000000";
        Kansalaisuus kansalaisuus = parser.parse(value);
        assertEquals(Muutostapa.LISATTY, kansalaisuus.getMuutostapa());
        assertEquals("FIN", kansalaisuus.getCode());
        assertEquals(LocalDate.of(2020, 10, 21), kansalaisuus.getStartDate());
        assertNull(kansalaisuus.getEndDate());
        assertTrue(kansalaisuus.getValid());
    }

    @Test
    public void serializesKansalaisuus() {
        String value = "0071FIN12020102100000000";
        Kansalaisuus kansalaisuus = parser.parse(value);
        String serialized = parser.serialize(kansalaisuus);
        assertEquals(value, serialized);
    }

    @Test
    public void parsesAndSerializesKansalaisuusWithAdditionalInformation() {
        String value = "007199812020102100000000";
        String additionalInformation = "4520Kosovo                        ";
        Kansalaisuus kansalaisuus = parser.parse(value, additionalInformation);
        assertEquals("Kosovo", kansalaisuus.getCode());
        String expected = String.join("|", value, additionalInformation);
        assertEquals(expected, parser.serialize(kansalaisuus));
    }
}
