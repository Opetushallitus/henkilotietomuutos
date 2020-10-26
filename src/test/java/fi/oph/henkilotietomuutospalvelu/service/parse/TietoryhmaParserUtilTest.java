package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkilotunnuksetonHenkilo;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TietoryhmaParserUtilTest {

    @Test
    public void parseHetutonHenkiloWithValidCountryCode() {
        String huoltajaRaw = "3051111177-094V36 1201702140000000020170220";
        String hetutonHenkiloRaw = "4510197002222Waltman                                                                    "
                + "                         Vanessa                                                                  "
                + "                           752";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(huoltajaRaw, hetutonHenkiloRaw);
        Assert.assertTrue(ryhma instanceof Huoltaja);

        Huoltaja huoltaja = (Huoltaja) ryhma;
        HenkilotunnuksetonHenkilo henkilo = huoltaja.getHenkilotunnuksetonHenkilo();
        Assert.assertEquals(Muutostapa.LISATIETO, henkilo.getMuutostapa());
        Assert.assertEquals(LocalDate.of(1970, 2, 22), henkilo.getDateOfBirth());
        Assert.assertEquals("Vanessa", henkilo.getFirstNames());
        Assert.assertEquals("Waltman", henkilo.getLastname());
        Assert.assertEquals(Gender.FEMALE, henkilo.getGender());
        Assert.assertEquals("752", henkilo.getNationality());
    }

    @Test
    public void parseHetutonHenkiloWithValidCountryCodeV20191201() {
        String huoltajaStr = "3051           212017022020330501\t0000000000000000";
        String henkilotunnuksetonHenkiloStr = "4510197002222Waltman                                                                    "
                + "                         Vanessa                                                                  "
                + "                           752";

        Tietoryhma tietoryhma = TietoryhmaParserUtil.deserializeTietoryhma(huoltajaStr, henkilotunnuksetonHenkiloStr);

        assertThat(tietoryhma).isInstanceOf(Huoltaja.class);
        Huoltaja huoltaja = (Huoltaja) tietoryhma;
        assertThat(huoltaja)
                .returns(Muutostapa.LISATTY, Huoltaja::getMuutostapa)
                .returns("", Huoltaja::getHetu)
                .returns("2", Huoltaja::getLaji)
                .returns("1", Huoltaja::getRooli)
                .returns(LocalDate.of(2017, 2,20), Huoltaja::getStartDate)
                .returns(LocalDate.of(2033, 5, 1), Huoltaja::getEndDate)
                .returns("", Huoltaja::getAsuminen)
                .returns(null, Huoltaja::getAsuminenAlkupvm)
                .returns(null, Huoltaja::getAsuminenLoppupvm);
        HenkilotunnuksetonHenkilo henkilo = huoltaja.getHenkilotunnuksetonHenkilo();
        assertThat(henkilo)
                .returns(Muutostapa.LISATIETO, HenkilotunnuksetonHenkilo::getMuutostapa)
                .returns(LocalDate.of(1970, 2, 22), HenkilotunnuksetonHenkilo::getDateOfBirth)
                .returns("Vanessa", HenkilotunnuksetonHenkilo::getFirstNames)
                .returns("Waltman", HenkilotunnuksetonHenkilo::getLastname)
                .returns(Gender.FEMALE, HenkilotunnuksetonHenkilo::getGender)
                .returns("752", HenkilotunnuksetonHenkilo::getNationality);
    }

    // This cannot exist alone
    @Test
    public void parseHetutonHenkiloTietoryhmaAlone() {
        String tietoryhma = "4510197002222Waltman                                                                    "
                + "                         Vanessa                                                                  "
                + "                           752";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertNull(ryhma);
    }

}
