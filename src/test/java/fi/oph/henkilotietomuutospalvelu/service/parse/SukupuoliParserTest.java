package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Sukupuoli;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SukupuoliParserTest {

    SukupuoliParser parser = new SukupuoliParser();

    @Test
    public void parsesSukupuoli() {
        String maleString = "00331";
        String femaleString = "00312";

        Sukupuoli male = parser.parse(maleString);
        Sukupuoli female = parser.parse(femaleString);

        assertEquals(Muutostapa.MUUTETTU, male.getMuutostapa());
        assertEquals(Gender.MALE, male.getGender());

        assertEquals(Muutostapa.LISATTY, female.getMuutostapa());
        assertEquals(Gender.FEMALE, female.getGender());
    }

    @Test
    public void serializesSukupuoli() {
        String maleString = "00331";
        String femaleString = "00312";

        Sukupuoli male = parser.parse(maleString);
        Sukupuoli female = parser.parse(femaleString);

        assertEquals(maleString, parser.serialize(male));
        assertEquals(femaleString, parser.serialize(female));
    }

}
