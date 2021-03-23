package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.TilapainenUlkomainenOsoite;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenOsoite;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UlkomainenOsoiteParserTest {

    UlkomainenOsoiteParser parser = new UlkomainenOsoiteParser();

    @Test
    public void parsesUlkomainenOsoite() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        1231970010100000000";
        UlkomainenOsoite osoite = parser.parse(tietoryhma);
        assertEquals(Ryhmatunnus.ULKOMAINEN_OSOITE, osoite.getRyhmatunnus());
        assertEquals("123", osoite.getCountryCode());
        assertEquals("Abcdef", osoite.getStreetAddress());
        assertEquals("Asdfasdf", osoite.getMunicipality());
        assertEquals(LocalDate.of(1970, 1, 1), osoite.getStartDate());
        assertNull(osoite.getEndDate());
    }

    @Test
    public void serializesUlkomainenOsoite() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        1231970010100000000";
        UlkomainenOsoite osoite = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(osoite));
    }

    @Test
    public void parsesUlkomainenOsoiteWithAdditionalInformation() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        9981970010100000000";
        String lisatieto = "4520Kosovo                        ";
        UlkomainenOsoite osoite = parser.parse(tietoryhma, lisatieto);
        assertEquals("998", osoite.getCountryCode());
        assertEquals("Kosovo", osoite.getAdditionalInformation());
    }

    @Test
    public void serializesUlkomainenOsoiteWithAdditionalInformation() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        9981970010100000000";
        String lisatieto = "4520Kosovo                        ";
        UlkomainenOsoite osoite = parser.parse(tietoryhma, lisatieto);
        String expected = String.join("|", tietoryhma, lisatieto);
        assertEquals(expected, parser.serialize(osoite));
    }

    @Test
    public void parsesAndSerializesTilapainenUlkomainenOsoite() {
        String tietoryhma = "1051Abcdef                                                                          Asdfasdf                                                                        1231970010100000000";
        TilapainenUlkomainenOsoite osoite = TilapainenUlkomainenOsoite.from(
                parser.parse(tietoryhma));
        assertEquals(Ryhmatunnus.ULKOMAINEN_OSOITE_TILAPAINEN, osoite.getRyhmatunnus());
        assertEquals(tietoryhma, parser.serialize(osoite));
    }
}
