package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Oikeus;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HuoltajaParserTest {
    
    private final HuoltajaParser parser = new HuoltajaParser();

    @Test
    public void parsesHuoltaja() {
        String tietoryhma = "3051111177-094V36 1201702140000000020170220";
        Huoltaja huoltaja = parser.parse(tietoryhma);
        assertEquals(Muutostapa.LISATTY, huoltaja.getMuutostapa());
        assertEquals("111177-094V", huoltaja.getHetu());
        assertEquals(LocalDate.of(2017, 2,14), huoltaja.getStartDate());
        assertNull(huoltaja.getEndDate());
    } // serialisointi ei tuettu vanhalla formaatilla!

    @Test
    public void parsesHuoltajaV20191201() {
        String huoltajaStr = "3051081184-175B212017022020330501 0000000000000000";
        String oikeudetStr1 = "3201P5012018031220180414";
        String oikeudetStr2 = "3204T3012019010120321231";
        Huoltaja huoltaja = parser.parse(huoltajaStr, oikeudetStr1, oikeudetStr2);
        assertThat(huoltaja)
                .returns(Muutostapa.LISATTY, Huoltaja::getMuutostapa)
                .returns("081184-175B", Huoltaja::getHetu)
                .returns("2", Huoltaja::getLaji)
                .returns("1", Huoltaja::getRooli)
                .returns(LocalDate.of(2017, 2,20), Huoltaja::getStartDate)
                .returns(LocalDate.of(2033, 5, 1), Huoltaja::getEndDate)
                .returns("", Huoltaja::getAsuminen)
                .returns(null, Huoltaja::getAsuminenAlkupvm)
                .returns(null, Huoltaja::getAsuminenLoppupvm);
        assertThat(huoltaja.getOikeudet()).extracting(Oikeus::getMuutostapa, Oikeus::getKoodi, Oikeus::getAlkupvm, Oikeus::getLoppupvm)
                .containsExactlyInAnyOrder(
                        tuple(Muutostapa.LISATTY, "P501", LocalDate.of(2018, 3, 12), LocalDate.of(2018, 4, 14)),
                        tuple(Muutostapa.POISTETTU, "T301", LocalDate.of(2019, 1, 1), LocalDate.of(2032, 12, 31)));
    }

    @Test
    public void serializesHuoltajaV20191201() {
        String huoltajaStr = "3051081184-175B212017022020330501 0000000000000000";
        String oikeudetStr1 = "3201P5012018031220180414";
        String oikeudetStr2 = "3204T3012019010120321231";
        Huoltaja huoltaja = parser.parse(huoltajaStr, oikeudetStr1, oikeudetStr2);
        String serialized = parser.serialize(huoltaja);
        assertTrue(serialized.startsWith(huoltajaStr));
        // oikeudet serialisoituvat määrittämättömässä järjestyksessä?
        assertTrue(serialized.contains(oikeudetStr1));
        assertTrue(serialized.contains(oikeudetStr2));
    }

    @Test
    public void parsesHetutonHuoltajaWithNonstandardNationality() {
        String huoltajaStr = "3051           212017022020330501 0000000000000000";
        String oikeudetStr = "3201P5012018031220180414";
        String hetutonStr = "451019700101 "
            + "Androgynous                                                                                         "
            + "Amorphous                                                                                           "
            + "998";
        String lisatietoStr = "4520Kosovo                        ";
        Huoltaja huoltaja = parser.parse(huoltajaStr, oikeudetStr, hetutonStr, lisatietoStr);
        assertEquals("", huoltaja.getHetu());
        assertEquals(LocalDate.of(2017, 2,20), huoltaja.getStartDate());
        assertEquals(LocalDate.of(2033, 5, 1), huoltaja.getEndDate());
        assertEquals("Androgynous", huoltaja.getHenkilotunnuksetonHenkilo().getLastname());
        assertEquals("Amorphous", huoltaja.getHenkilotunnuksetonHenkilo().getFirstNames());
        assertEquals("998", huoltaja.getHenkilotunnuksetonHenkilo().getNationality());
        assertEquals("Kosovo", huoltaja.getHenkilotunnuksetonHenkilo().getAdditionalInformation());
    }

    @Test
    public void serializesHuoltajaWithNonstandardNationality() {
        String huoltajaStr = "3051           212017022020330501 0000000000000000";
        String oikeudetStr = "3201P5012018031220180414";
        String hetutonStr = "451019700101 "
                + "Androgynous                                                                                         "
                + "Amorphous                                                                                           "
                + "998";
        String lisatietoStr = "4520Kosovo                        ";
        Huoltaja huoltaja = parser.parse(huoltajaStr, oikeudetStr, hetutonStr, lisatietoStr);
        String expected = String.join("|", huoltajaStr, hetutonStr, lisatietoStr, oikeudetStr);
        assertEquals(expected, parser.serialize(huoltaja));
    }

}
