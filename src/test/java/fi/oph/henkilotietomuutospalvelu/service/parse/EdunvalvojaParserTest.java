package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvoja;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EdunvalvojaParserTest {

    EdunvalvojaParser parser = new EdunvalvojaParser();

    @Test
    public void parsesEdunvalvoja() {
        String tietoryhma = "3071071057-108S         0000000000000000000000000";
        Edunvalvoja valvoja = parser.parse(tietoryhma);
        assertEquals(Muutostapa.LISATTY, valvoja.getMuutostapa());
        assertEquals("071057-108S", valvoja.getHetu());
        assertEquals("000", valvoja.getMunicipalityCode());
        assertEquals("000000", valvoja.getOikeusaputoimistoKoodi());
        assertEquals("", valvoja.getYTunnus());
        assertNull(valvoja.getStartDate());
        assertNull(valvoja.getEndDate());
    }

    @Test
    public void serializesEdunvalvoja() {
        String tietoryhma = "3071071057-108S         0000000000000000000000000";
        Edunvalvoja valvoja = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(valvoja));
    }
}
