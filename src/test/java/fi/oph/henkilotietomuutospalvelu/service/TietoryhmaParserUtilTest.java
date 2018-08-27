package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.dto.type.*;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class TietoryhmaParserUtilTest {

    @Test
    public void parseAidinkieliWhenLanguageCodeIsValid() {
        String tietoryhmaStr = "0021fi";
        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhmaStr);
        Assert.assertTrue(ryhma instanceof Aidinkieli);

        Aidinkieli aidinkieli = (Aidinkieli) ryhma;
        Assert.assertEquals("fi", aidinkieli.getLanguageCode());
    }

    @Test
    public void parseAidinkieliWhenLanguageCodeContainsAdditionalInformation() {
        String tietoryhmaStr = "002198";
        String lisatiedot = "4520suomalainen viittomakieli     ";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhmaStr, lisatiedot);
        Assert.assertTrue(ryhma instanceof Aidinkieli);

        Aidinkieli aidinkieli = (Aidinkieli) ryhma;
        Assert.assertEquals("98", aidinkieli.getLanguageCode());
        Assert.assertEquals("suomalainen viittomakieli", aidinkieli.getAdditionalInformation());
    }

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
        Assert.assertEquals(LocalDate.of(2017, 2, 20), huoltaja.getResolutionDate());
        Assert.assertEquals(null, huoltaja.getEndDate());
        Assert.assertEquals(true, huoltaja.getVoimassa());
        Assert.assertEquals(Huollonjako.SUORITTAMATTA, huoltaja.getHuollonjako());
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
    public void parseHenkilotunnuskorjaus() {
        String uusiTunnus = "0011060622-451X1";
        String vanhaTunnus = "0013030622-123D2";

        Tietoryhma uusiRyhma = TietoryhmaParserUtil.deserializeTietoryhma(uusiTunnus);
        Assert.assertTrue(uusiRyhma instanceof Henkilotunnuskorjaus);

        Henkilotunnuskorjaus korjaus = (Henkilotunnuskorjaus) uusiRyhma;
        Assert.assertEquals(Muutostapa.LISATTY, korjaus.getMuutostapa());
        Assert.assertEquals("060622-451X", korjaus.getHetu());
        Assert.assertEquals(true, korjaus.getActive());

        Tietoryhma vanhaRyhma = TietoryhmaParserUtil.deserializeTietoryhma(vanhaTunnus);
        Assert.assertTrue(vanhaRyhma instanceof Henkilotunnuskorjaus);

        korjaus = (Henkilotunnuskorjaus) vanhaRyhma;
        Assert.assertEquals(Muutostapa.MUUTETTU, korjaus.getMuutostapa());
        Assert.assertEquals("030622-123D", korjaus.getHetu());
        Assert.assertEquals(false, korjaus.getActive());
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
        String tietoryhma = "4510197002222Waltman                                                                    "
                + "                         Vanessa                                                                  "
                + "                           752";

        Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhma);
        Assert.assertTrue(ryhma instanceof HenkilotunnuksetonHenkilo);

        HenkilotunnuksetonHenkilo henkilo = (HenkilotunnuksetonHenkilo) ryhma;
        Assert.assertEquals(Muutostapa.LISATIETO, henkilo.getMuutostapa());
        Assert.assertEquals(LocalDate.of(1970, 2, 22), henkilo.getDateOfBirth());
        Assert.assertEquals("Vanessa", henkilo.getFirstNames());
        Assert.assertEquals("Waltman", henkilo.getLastname());
        Assert.assertEquals(Gender.FEMALE, henkilo.getGender());
        Assert.assertEquals("752", henkilo.getNationality());
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
