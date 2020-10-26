package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.SahkopostiOsoite;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SahkopostiOsoiteParserTest {

    @Test
    public void parsesSahkopostiOsoite() {
        String tietoryhma = "421199katariina@kokkokolmonen.com                                                                                                                                                                                                                                    2017022200000000";
        SahkopostiOsoite osoite = SahkopostiOsoiteParser.parseSahkopostiOsoite(tietoryhma);
        assertEquals(Muutostapa.LISATTY, osoite.getMuutostapa());
        assertEquals("99", osoite.getLajikoodi());
        assertEquals("katariina@kokkokolmonen.com", osoite.getEmail());
        assertEquals(LocalDate.of(2017, 2, 22), osoite.getStartDate());
        assertNull(osoite.getEndDate());

        String toinenTietoryhma = "421399aune41@saad.net                                                                                                                                                                                                                                                2016050920170222";
        SahkopostiOsoite toinenOsoite = SahkopostiOsoiteParser.parseSahkopostiOsoite(toinenTietoryhma);
        assertEquals(Muutostapa.MUUTETTU, toinenOsoite.getMuutostapa());
        assertEquals("99", toinenOsoite.getLajikoodi());
        assertEquals("aune41@saad.net", toinenOsoite.getEmail());
        assertEquals(LocalDate.of(2016, 5, 9), toinenOsoite.getStartDate());
        assertEquals(LocalDate.of(2017, 2, 22), toinenOsoite.getEndDate());
    }

    @Test
    public void serializesSahkopostiOsoite() {
        String tietoryhma = "421199katariina@kokkokolmonen.com                                                                                                                                                                                                                                    2017022200000000";
        SahkopostiOsoite osoite = SahkopostiOsoiteParser.parseSahkopostiOsoite(tietoryhma);
        assertEquals(tietoryhma, SahkopostiOsoiteParser.serializeSahkopostiOsoite(osoite));
    }

}
