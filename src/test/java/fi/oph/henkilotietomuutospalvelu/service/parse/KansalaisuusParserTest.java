package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kansalaisuus;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class KansalaisuusParserTest {

    @Test
    public void parsesKansalaisuus() {
        String value = "0071FIN12020102100000000";
        Kansalaisuus kansalaisuus = KansalaisuusParser.parseKansalaisuus(value);
        assertEquals(Muutostapa.LISATTY, kansalaisuus.getMuutostapa());
        assertEquals("FIN", kansalaisuus.getCode());
        assertEquals(LocalDate.of(2020, 10, 21), kansalaisuus.getStartDate());
        assertNull(kansalaisuus.getEndDate());
        assertTrue(kansalaisuus.getValid());
    }

    @Test
    public void serializesKansalaisuus() {
        String value = "0071FIN12020102100000000";
        Kansalaisuus kansalaisuus = KansalaisuusParser.parseKansalaisuus(value);
        String serialized = KansalaisuusParser.serializeKansalaisuus(kansalaisuus);
        assertEquals(value, serialized);
    }

    @Test
    public void parsesAndSerializesKansalaisuusWithAdditionalInformation() {
        String value = "007199812020102100000000";
        String additionalInformation = "4520Kosovo                        ";
        Kansalaisuus kansalaisuus = KansalaisuusParser.parseKansalaisuus(value, additionalInformation);
        assertEquals("Kosovo", kansalaisuus.getCode());
        String expected = String.join("|", value, additionalInformation);
        assertEquals(expected, KansalaisuusParser.serializeKansalaisuus(kansalaisuus));
    }
}
