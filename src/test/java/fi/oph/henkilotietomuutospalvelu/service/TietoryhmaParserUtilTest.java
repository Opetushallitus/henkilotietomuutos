package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;
import fi.oph.henkilotietomuutospalvelu.dto.type.Toimintakelpoisuus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class TietoryhmaParserUtilTest {

    @Test
    public void parseAmmatti() {
        String tietoryhma = "4013    luottoneuvottelija                 ";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof Ammatti);

        Ammatti ammatti = (Ammatti) ryhma;
        Assert.assertEquals(Muutostapa.MUUTETTU, ammatti.getMuutostapa());
        Assert.assertEquals("", ammatti.getCode());
        Assert.assertEquals("luottoneuvottelija", ammatti.getDescription());
    }

    @Test
    public void parseEdunvalvoja() {
        String tietoryhma = "3071071057-108S         0000000000000000000000000";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof Edunvalvoja);

        Edunvalvoja valvoja = (Edunvalvoja) ryhma;
        Assert.assertEquals(Muutostapa.LISATTY, valvoja.getMuutostapa());
        Assert.assertEquals("071057-108S", valvoja.getHetu());
        Assert.assertEquals("000", valvoja.getMunicipalityCode());
        Assert.assertEquals("000000", valvoja.getOikeusaputoimistoKoodi());
        Assert.assertEquals("", valvoja.getYTunnus());
        Assert.assertNull(valvoja.getStartDate());
        Assert.assertNull(valvoja.getEndDate());
    }

    @Test
    public void parseEdunvalvonta() {
        String tietoryhma = "30601989020100000000 102|3075                    0000000001989020119980101";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof Edunvalvonta);

        Edunvalvonta valvonta = (Edunvalvonta) ryhma;
        Assert.assertEquals(Muutostapa.LISATIETO, valvonta.getMuutostapa());
        Assert.assertEquals(LocalDate.of(1989, 2, 1), valvonta.getStartDate());
        Assert.assertNull(valvonta.getEndDate());
        Assert.assertEquals(false, valvonta.getDutiesStarted());
        Assert.assertEquals(Toimintakelpoisuus.RAJOITTAMATON, valvonta.getEdunvalvontatieto());
        Assert.assertEquals(new Long(2), valvonta.getEdunvalvojat());
    }

    @Test
    public void parseSahkopostiOsoite() {
        String tietoryhma = "421199katariina@kokkokolmonen.com                                                                                                                                                                                                                                    2017022200000000";
        String toinenTietoryhma = "421399aune41@saad.net                                                                                                                                                                                                                                                2016050920170222";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof SahkopostiOsoite);

        SahkopostiOsoite osoite = (SahkopostiOsoite) ryhma;
        Assert.assertEquals(Muutostapa.LISATTY, osoite.getMuutostapa());
        Assert.assertEquals("99", osoite.getLajikoodi());
        Assert.assertEquals("katariina@kokkokolmonen.com", osoite.getEmail());
        Assert.assertEquals(LocalDate.of(2017, 2, 22), osoite.getStartDate());
        Assert.assertNull(osoite.getEndDate());


        Tietoryhma toinenRyhma = TietoryhmaParserUtil.deserializeTietoryhma(toinenTietoryhma);

        SahkopostiOsoite toinenOsoite = (SahkopostiOsoite) toinenRyhma;
        Assert.assertEquals(Muutostapa.MUUTETTU, toinenOsoite.getMuutostapa());
        Assert.assertEquals("99", toinenOsoite.getLajikoodi());
        Assert.assertEquals("aune41@saad.net", toinenOsoite.getEmail());
        Assert.assertEquals(LocalDate.of(2016, 5, 9), toinenOsoite.getStartDate());
        Assert.assertEquals(LocalDate.of(2017, 2, 22), toinenOsoite.getEndDate());
    }

    @Test
    public void parseHuoltaja() {
        String tietoryhma = "3051111177-094V36 1201702140000000020170220";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof Huoltaja);

        Huoltaja huoltaja = (Huoltaja) ryhma;
        Assert.assertEquals(Muutostapa.LISATTY, huoltaja.getMuutostapa());
        Assert.assertEquals("111177-094V", huoltaja.getHetu());
        Assert.assertEquals(LocalDate.of(2017, 2,14), huoltaja.getStartDate());
        Assert.assertNull(huoltaja.getEndDate());
    }

    @Test
    public void parseHuoltajaV20191201() {
        String huoltajaStr = "3051081184-175B212017022020330501\t0000000000000000";
        String oikeudetStr1 = "3201P5012018031220180414";
        String oikeudetStr2 = "3204T3012019010120321231";

        Tietoryhma tietoryhma = TietoryhmaParserUtil.deserializeTietoryhma(huoltajaStr, oikeudetStr1, oikeudetStr2);

        assertThat(tietoryhma).isInstanceOf(Huoltaja.class);
        Huoltaja huoltaja = (Huoltaja) tietoryhma;
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
    public void parseHenkiloName() {
        String firstTietoryhmaString = "0045Miehenä Tes                                                                                         Ukko Antto                                                                                          00000000 ";
        String secondTietoryhmaString = "0046Miehenä Tes                                                                                         Ukko Anton                                                                                          00000000 ";

        Tietoryhma firstTietoryhma = TietoryhmaParserUtil.deserializeTietoryhma(firstTietoryhmaString);
        Tietoryhma secondTietoryhma = TietoryhmaParserUtil.deserializeTietoryhma(secondTietoryhmaString);

        Assert.assertTrue(firstTietoryhma instanceof HenkiloName);
        Assert.assertTrue(secondTietoryhma instanceof  HenkiloName);

        HenkiloName oldName = (HenkiloName) firstTietoryhma;
        HenkiloName newName = (HenkiloName) secondTietoryhma;

        Assert.assertEquals(Muutostapa.KORJATTAVAA, oldName.getMuutostapa());
        Assert.assertNull(newName.getLastUpdateDate());

        Assert.assertEquals(Muutostapa.KORJATTU, newName.getMuutostapa());
        Assert.assertNull(newName.getLastUpdateDate());

        Assert.assertEquals("Ukko Anton", newName.getFirstNames());
        Assert.assertEquals("Ukko Antto", oldName.getFirstNames());
        Assert.assertEquals("Miehenä Tes", newName.getLastName());
        Assert.assertEquals("Miehenä Tes", oldName.getLastName());
    }

    @Test
    public void parseHenkiloNameChange() {
        String firstTietoryhmaString = "0051Olli Santeri                                                                                        022017010100000000 ";
        String secondTietoryhmaString = "0053Outi Susanna                                                                                        060000000020170101 ";

        Tietoryhma firstTietoryhma = TietoryhmaParserUtil.deserializeTietoryhma(firstTietoryhmaString);
        Tietoryhma secondTietoryhma = TietoryhmaParserUtil.deserializeTietoryhma(secondTietoryhmaString);

        Assert.assertTrue(firstTietoryhma instanceof HenkiloNameChange);
        Assert.assertTrue(secondTietoryhma instanceof  HenkiloNameChange);

        HenkiloNameChange newName = (HenkiloNameChange) firstTietoryhma;
        HenkiloNameChange oldName = (HenkiloNameChange) secondTietoryhma;

        Assert.assertEquals(Muutostapa.LISATTY, newName.getMuutostapa());
        Assert.assertEquals(LocalDate.of(2017, 1, 1), newName.getStartDate());
        Assert.assertNull(newName.getEndDate());

        Assert.assertEquals(Muutostapa.MUUTETTU, oldName.getMuutostapa());
        Assert.assertNull(oldName.getStartDate());
        Assert.assertEquals(LocalDate.of(2017, 1, 1), oldName.getEndDate());

        Assert.assertEquals("Olli Santeri", newName.getName());
        Assert.assertEquals("Outi Susanna", oldName.getName());
        Assert.assertEquals(NameType.ETUNIMI, newName.getNameType());
        Assert.assertEquals(NameType.ETUNIMI, oldName.getNameType());
    }

    @Test
    public void parseSukupuoli() {
        String maleString = "00331";
        String femaleString = "00312";

        Tietoryhma maleRyhma = TietoryhmaParserUtil.deserializeTietoryhma(maleString);
        Tietoryhma femaleRyhma = TietoryhmaParserUtil.deserializeTietoryhma(femaleString);

        Assert.assertTrue(maleRyhma instanceof Sukupuoli);
        Assert.assertTrue(femaleRyhma instanceof Sukupuoli);

        Sukupuoli male = (Sukupuoli) maleRyhma;
        Sukupuoli female = (Sukupuoli) femaleRyhma;

        Assert.assertEquals(Muutostapa.MUUTETTU, male.getMuutostapa());
        Assert.assertEquals(Gender.MALE, male.getGender());

        Assert.assertEquals(Muutostapa.LISATTY, female.getMuutostapa());
        Assert.assertEquals(Gender.FEMALE, female.getGender());
    }

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

    @Test
    public void parseShortenedTietoryhma() {
        String tietoryhma = "0091050Dhaka";
        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof UlkomainenSyntymapaikka);

        UlkomainenSyntymapaikka syntymapaikka = (UlkomainenSyntymapaikka) ryhma;
        Assert.assertEquals("050", syntymapaikka.getCountryCode());
        Assert.assertEquals("Dhaka", syntymapaikka.getLocation());
    }
}
