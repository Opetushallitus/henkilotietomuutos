package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenHenkilonumero;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UlkomainenHenkilonumeroParserTest {

    UlkomainenHenkilonumeroParser parser = new UlkomainenHenkilonumeroParser();

    @Test
    public void parsesUlkomainenHenkilonumero() {
        String tietoryhma = "422112345                         21231 1200101010000000000000000000000001";
        UlkomainenHenkilonumero henkilonumero = parser.parse(tietoryhma);
        assertEquals("123", henkilonumero.getCountryCode());
        assertEquals(Gender.FEMALE, henkilonumero.getGender());
        assertEquals(LocalDate.of(2001, 1, 1), henkilonumero.getIssueDate());
        assertEquals("12345", henkilonumero.getUlkomainenHenkilonumeroId());
        assertEquals("1", henkilonumero.getTietolahde());
        assertTrue(henkilonumero.getValid());
        assertTrue(henkilonumero.getValidVTJ());
    }

    @Test
    public void serializesUlkomainenHenkilonumero() {
        String tietoryhma = "422112345                         21231 1200101010000000000000000000000001";
        UlkomainenHenkilonumero henkilonumero = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(henkilonumero));
    }
}
