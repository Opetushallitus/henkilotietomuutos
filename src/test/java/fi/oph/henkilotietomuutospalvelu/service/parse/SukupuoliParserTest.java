package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Sukupuoli;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SukupuoliParserTest {

    @Test
    public void parsesSukupuoli() {
        String maleString = "00331";
        String femaleString = "00312";

        Sukupuoli male = SukupuoliParser.parseSukupuoli(maleString);
        Sukupuoli female = SukupuoliParser.parseSukupuoli(femaleString);

        assertEquals(Muutostapa.MUUTETTU, male.getMuutostapa());
        assertEquals(Gender.MALE, male.getGender());

        assertEquals(Muutostapa.LISATTY, female.getMuutostapa());
        assertEquals(Gender.FEMALE, female.getGender());
    }

    @Test
    public void serializesSukupuoli() {
        String maleString = "00331";
        String femaleString = "00312";

        Sukupuoli male = SukupuoliParser.parseSukupuoli(maleString);
        Sukupuoli female = SukupuoliParser.parseSukupuoli(femaleString);

        assertEquals(maleString, SukupuoliParser.serializeSukupuoli(male));
        assertEquals(femaleString, SukupuoliParser.serializeSukupuoli(female));
    }

}
