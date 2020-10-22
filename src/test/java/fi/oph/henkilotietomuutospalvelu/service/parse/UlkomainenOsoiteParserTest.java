package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenOsoite;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UlkomainenOsoiteParserTest {

    @Test
    public void parsesUlkomainenOsoite() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        1231970010100000000";
        UlkomainenOsoite osoite = UlkomainenOsoiteParser.parseUlkomainenOsoite(tietoryhma);
        assertEquals("123", osoite.getCountryCode());
        assertEquals("Abcdef", osoite.getStreetAddress());
        assertEquals("Asdfasdf", osoite.getMunicipality());
        assertEquals(LocalDate.of(1970, 1, 1), osoite.getStartDate());
        assertNull(osoite.getEndDate());
    }

    @Test
    public void serializesUlkomainenOsoite() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        1231970010100000000";
        UlkomainenOsoite osoite = UlkomainenOsoiteParser.parseUlkomainenOsoite(tietoryhma);
        assertEquals(tietoryhma, UlkomainenOsoiteParser.serializeUlkomainenOsoite(osoite));
    }

    @Test
    public void parsesUlkomainenOsoiteWithAdditionalInformation() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        9981970010100000000";
        String lisatieto = "4520Kosovo                        ";
        UlkomainenOsoite osoite = UlkomainenOsoiteParser.parseUlkomainenOsoite(tietoryhma, lisatieto);
        assertEquals("998", osoite.getCountryCode());
        assertEquals("Kosovo", osoite.getAdditionalInformation());
    }

    @Test
    public void serializesUlkomainenOsoiteWithAdditionalInformation() {
        String tietoryhma = "1041Abcdef                                                                          Asdfasdf                                                                        9981970010100000000";
        String lisatieto = "4520Kosovo                        ";
        UlkomainenOsoite osoite = UlkomainenOsoiteParser.parseUlkomainenOsoite(tietoryhma, lisatieto);
        String expected = String.join("|", tietoryhma, lisatieto);
        assertEquals(expected, UlkomainenOsoiteParser.serializeUlkomainenOsoite(osoite));
    }
}
