package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.dto.type.*;
import fi.oph.henkilotietomuutospalvelu.service.exception.TietoryhmaParseException;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;

import java.util.Arrays;

@Slf4j
public class TietoryhmaParserUtil {

    public static Tietoryhma deserializeTietoryhma(String tietoryhma) {
        return deserializeTietoryhma(tietoryhma, null);
    }

    public static Tietoryhma deserializeTietoryhma(String tietoryhma, String additionalInformation) {
        if (tietoryhma.length() < 4) {
            throw new TietoryhmaParseException("Tietoryhmä has a length less than 4 and is not valid!");
        }
        String ryhmatunnus = parseString(tietoryhma, 0, 3);
        switch (ryhmatunnus) {
            case("452"): // 4.4.2 Selväkielinen tieto
                // Skip, as we already read these in the previous tietoryhma.
                return null;

            case("001"):
                return parseHenkilotunnuskorjaus(tietoryhma);
            case("002"):
                return parseAidinkieli(tietoryhma, additionalInformation);
            case("003"):
                return parseSukupuoli(tietoryhma);
            case("004"):
                return parseHenkiloName(tietoryhma);
            case("005"):
                return parseHenkiloNameChange(tietoryhma);
            case("007"):
                return parseKansalaisuus(tietoryhma, additionalInformation);
            case("008"):
                return parseSyntymaKotikunta(tietoryhma);
            case("009"):
                return parseUlkomainenSyntymapaikka(tietoryhma, additionalInformation);
            case("013"):
                return parseKuolinpaiva(tietoryhma);
            case("015"):
                return parseTurvakielto(tietoryhma);
            case("101"):
                return parseKotimainenOsoite(tietoryhma);
            case("102"):
                return parseTilapainenKotimainenOsoite(tietoryhma);
            case("103"):
                return parsePostiosoite(tietoryhma);
            case("104"):
                return parseUlkomainenOsoite(tietoryhma, additionalInformation);
            case("105"):
                return parseTilapainenUlkomainenOsoite(tietoryhma, additionalInformation);
            case("204"):
                return parseKotikunta(tietoryhma);
            case("305"):
                return parseHuoltaja(tietoryhma);
            case("306"):
                return parseEdunvalvonta(tietoryhma);
            case("307"):
                return parseEdunvalvoja(tietoryhma);
            case("316"):
                return parseEdunvalvontaValtuutus(tietoryhma);
            case("317"):
                return parseEdunvalvontaValtuutettu(tietoryhma);
            case("401"):
                return parseAmmatti(tietoryhma);
            case("421"):
                return parseSahkopostiOsoite(tietoryhma);
            case("422"):
                return parseUlkomainenHenkilonumero(tietoryhma);
            case("423"):
                return parseKutsumanimi(tietoryhma);
            case("451"):
                return parseHenkilotunnuksetonHenkilo(tietoryhma, additionalInformation);

            case("006"): // 3.1.6 Nimenmuutostapa (välitetään vain seurakunnille)
            case("010"): // 3.1.10 Siviilisääty
            case("011"): // 3.1.11 Nykyinen paikallinen rekisteriviranomainen
            case("012"): // 3.1.12 Historiaa paikallisista rekisteriviranomaisista
            case("014"): // 3.1.14 Kuolleeksijulistamispäivä
            case("016"): // 3.1.16 Muut tietojenluovutuskiellot (välitetään vain seurakunnille)
            case("201"): // 3.3.1 Vakituinen kotipaikkatunnus. Tietoryhmä poistuu pl. kunnat
            case("205"): // 3.3.3 Edellinen kotikunta
            case("206"): // 3.3.4 Tilapäinen kunta
            case("208"): // 3.3.5 Vuodenvaihteen kunta
            case("209"): // 3.3.6 Poissaolo
            case("210"): // 3.3.7 Suomeen muuttopäivä
            case("211"): // 3.3.8 Valtio, josta muuttanut
            case("212"): // 3.3.9 Valtio, johon muuttanut
            case("213"): // 3.3.10 Väestökirjanpitokunta
            case("241"): // 3.3.11 Vakinainen asuminen Suomessa
            case("242"): // 3.3.12 Edellinen vakinainen asuminen Suomessa
            case("243"): // 3.3.13 Tilapäinen asuminen Suomessa
            case("251"): // 3.3.14 Ulkomailla ja ns. 900-ryhmissä olevien vakinainen asuminen
            case("252"): // 3.3.15 Edellinen ulkomailla ja ns. 900-ryhmissä olevien vakinainen asuminen
            case("253"): // 3.3.16 Tilapäinen ulkomailla ja ns. 900-ryhmissä olevien asuminen
            case("301"): // 4.1.1 Lapsi (päähenkilönä vanhempi)
            case("302"): // 4.1.2 Vanhempi (päähenkilönä lapsi)
            case("303"): // 4.1.3 Ottolapsisuhde
            case("304"): // 4.1.4 Huollettava (päähenkilönä huoltaja)
            case("308"): // 4.1.8 Huostaanotto
            case("309"): // 4.1.9 Perhesuhde (vain perustietona)
            case("351"): // 4.2.1 Avioliitto
            case("352"): // 4.2.2 Järjestysnumero ja vihkitapa
            case("353"): // 4.2.3 Päättynyt avioliitto
            case("354"): // 4.2.4 Asumusero
            case("355"): // 4.2.5 Rekisteröity parisuhde
            case("402"): // 4.3.2 Uskontokunta
            case("403"): // 4.3.3 Historiaa uskontokunnasta
            case("405"): // 4.3.4 Järjestyskirjain
            case("407"): // 4.3.5 Uskontokuntalaji
            case("410"): // 4.3.6 Asiointikieli
            case("420"): // 4.3.7 Sähköinen asiointitunnus
            case("424"): // 4.3.11 Kotimainen yhteysosoite
            case("425"): // 4.3.12 Ulkomainen yhteysosoite
            case("453"): // 4.4.3 Tarkentava nimi (välitetään vain lisäpalveluna)

            // Lomakkeet
            case("471"): // 4.5.1 XAA:lta välitettävät tiedot
            case("472"): // 4.5.2 XBL:ltä välitettävät tiedot
            case("473"): // 4.5.3 XBO:lta välitettävät tiedot
            case("474"): // 4.5.4 XBV:ltä ja XPV:ltä välitettävät tiedot
            case("902"): // 6.1 Ortod. kirkolliset tiedot
            default:
                throw new TietoryhmaParseException("Unsupported Tietoryhma! Ryhmatunnus: " + ryhmatunnus);
        }
    }

    private static Henkilotunnuskorjaus parseHenkilotunnuskorjaus(String value) {
        return Henkilotunnuskorjaus.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILOTUNNUS_KORJAUS)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .active(parseCharacter(value, 15).equals("1")).build();
    }

    private static Sukupuoli parseSukupuoli(String value) {
        return Sukupuoli.builder()
                .ryhmatunnus(Ryhmatunnus.SUKUPUOLI)
                .muutostapa(parseMuutosTapa(value))
                .gender(Gender.getEnum(parseCharacter(value, 4)))
                .build();
    }

    private static HenkiloName parseHenkiloName(String value) {
        return HenkiloName.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILO_NIMI)
                .muutostapa(parseMuutosTapa(value))
                .lastName(parseString(value, 4, 100))
                .firstNames(parseString(value, 104, 100))
                .lastUpdateDate(parseDate(value, 204))
                .additionalInformation(parseCharacter(value, 212))
                .build();
    }

    private static Kutsumanimi parseKutsumanimi(String value) {
        return Kutsumanimi.builder()
                .ryhmatunnus(Ryhmatunnus.KUTSUMANIMI)
                .muutostapa(parseMuutosTapa(value))
                .name(parseString(value, 4,100))
                .type(parseString(value, 104,2 ))
                .startDate(parseDate(value, 106))
                .endDate(parseDate(value, 114))
                .nonStandardCharacters(parseCharacter(value, 122).equals("1"))
                .build();
    }

    private static HenkiloNameChange parseHenkiloNameChange(String value) {
        /*
         * Kuvaus: Henkilön nimen laji.
         * 01 = nykyinen sukunimi
         * 02 = nykyiset etunimet
         * 03 = nykyinen välinimi
         * 04 = kutsumanimi
         * 05 = entinen sukunimi
         * 06 = entiset etunimet
         * 07 = viimeksi naimattomana ollessa ollut sukunimi
         * 08 = entinen välinimi (ei oteta toistaiseksi käyttöön)
         * 09 = entinen kutsumanimi (ei oteta toistaiseksi käyttöön)
         * 10 = korjattu sukunimi
         * 11 = korjatut etunimet
         * 12 = korjattu välinimi
         * 13 = korjattu kutsumanimi (ei oteta toistaiseksi käyttöön)
         * 14 = patronyymi(ei oteta toistaiseksi käyttöön)
         * 15 = juridisen henkilön nimi
         */

        String laji = parseString(value, 104, 2);

        NameType type = NameType.TUNTEMATON;
        if (Arrays.asList("01", "05", "07", "10").contains(laji)) {
            type = NameType.SUKUNIMI;
        } else if (Arrays.asList("02", "06", "11").contains(laji)) {
            type = NameType.ETUNIMI;
        } else if (Arrays.asList("03", "08", "12").contains(laji)) {
            type = NameType.VALINIMI;
        } else if (Arrays.asList("04", "09", "13").contains(laji)) {
            type = NameType.KUTSUMANIMI;
        }

        return HenkiloNameChange.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILO_NIMENMUUTOS)
                .muutostapa(parseMuutosTapa(value))
                .name(parseString(value,4, 100))
                .nameType(type)
                .startDate(parseDate(value, 106))
                .endDate(parseDate(value, 114))
                .hasNonStandardCharacters(parseCharacter(value, 122).equals("1"))
                .build();
    }

    private static Kansalaisuus parseKansalaisuus(String value, String additionalInformation) {
        String code = parseString(value, 4, 3);
        if (code.equals("998")) {
            code = parseAdditionalInformation(additionalInformation);
        }

        return Kansalaisuus.builder()
                .ryhmatunnus(Ryhmatunnus.KANSALAISUUS)
                .muutostapa(parseMuutosTapa(value))
                .code(code)
                .valid(parseCharacter(value, 7).equals("1"))
                .startDate(parseDate(value, 8))
                .endDate(parseDate(value,16))
                .build();
    }

    private static SyntymaKotikunta parseSyntymaKotikunta(String value) {
        return SyntymaKotikunta.builder()
                .ryhmatunnus(Ryhmatunnus.SYNTYMAKOTIKUNTA)
                .muutostapa(parseMuutosTapa(value))
                .kuntakoodi(parseString(value, 4, 3))
                .build();
    }

    private static KotimainenOsoite parseKotimainenOsoite(String value) {
        return KotimainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.KOTIMAINEN_OSOITE)
                .muutostapa(parseMuutosTapa(value))
                .lahiosoite(parseString(value, 4, 100))
                .lahiosoiteSV(parseString(value,104, 100))
                .katunumero(parseString(value, 204, 7))
                .porraskirjain(parseCharacter(value,211))
                .huonenumero(parseString(value, 212, 3))
                .jakokirjain(parseCharacter(value, 215))
                .postinumero(parseString(value, 216, 5))
                .startDate(parseDate(value,221))
                .endDate(parseDate(value, 229))
                .build();
    }

    private static TilapainenKotimainenOsoite parseTilapainenKotimainenOsoite(String value) {
        return TilapainenKotimainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.KOTIMAINEN_OSOITE_TILAPAINEN)
                .muutostapa(parseMuutosTapa(value))
                .lahiosoite(parseString(value, 4, 100))
                .lahiosoiteSV(parseString(value,104, 100))
                .katunumero(parseString(value, 204, 7))
                .porraskirjain(parseCharacter(value,211))
                .huonenumero(parseString(value, 212, 3))
                .jakokirjain(parseCharacter(value, 215))
                .postinumero(parseString(value, 216, 5))
                .startDate(parseDate(value,221))
                .endDate(parseDate(value, 229))
                .build();
    }

    private static Postiosoite parsePostiosoite(String value) {
        return Postiosoite.builder()
                .ryhmatunnus(Ryhmatunnus.POSTIOSOITE)
                .muutostapa(parseMuutosTapa(value))
                .postiosoite(parseString(value, 4, 50))
                .postiosoiteSv(parseString(value, 54, 50))
                .postinumero(parseString(value, 104, 5))
                .startDate(parseDate(value, 109))
                .endDate(parseDate(value, 117))
                .build();
    }

    private static Edunvalvonta parseEdunvalvonta(String value) {
        return Edunvalvonta.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTA)
                .muutostapa(parseMuutosTapa(value))
                .startDate(parseDate(value, 4))
                .endDate(parseDate(value, 12))
                .dutiesStarted(parseCharacter(value, 20).equals("1"))
                .edunvalvontatieto(Toimintakelpoisuus.getEnum(parseCharacter(value, 21)))
                .edunvalvojat(Long.valueOf(parseString(value, 22, 2)))
                .build();
    }

    private static Edunvalvoja parseEdunvalvoja(String value) {
        return Edunvalvoja.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVOJA)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .yTunnus(parseString(value, 15, 9))
                .municipalityCode(parseString(value, 24, 3))
                .oikeusaputoimistoKoodi(parseString(value, 27, 6))
                .startDate(parseDate(value, 33))
                .endDate(parseDate(value, 41))
                .build();
    }

    private static EdunvalvontaValtuutus parseEdunvalvontaValtuutus(String value) {
        return EdunvalvontaValtuutus.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTAVALTUUTUS)
                .muutostapa(parseMuutosTapa(value))
                .startDate(parseDate(value, 4))
                .endDate(parseDate(value, 12))
                .dutiesStarted(parseCharacter(value, 20).equals("1"))
                .edunvalvojaValtuutetut(Long.valueOf(parseString(value, 21, 2)))
                .build();
    }

    private static EdunvalvontaValtuutettu parseEdunvalvontaValtuutettu(String value) {
        return EdunvalvontaValtuutettu.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTAVALTUUTETTU)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .startDate(parseDate(value, 15))
                .endDate(parseDate(value, 23))
                .build();
    }

    private static Kotikunta parseKotikunta(String value) {
        return Kotikunta.builder()
                .ryhmatunnus(Ryhmatunnus.KOTIKUNTA)
                .muutostapa(parseMuutosTapa(value))
                .code(parseString(value, 4, 3))
                .moveDate(parseDate(value, 7))
                .build();
    }

    private static Huoltaja parseHuoltaja(String value) {
        return Huoltaja.builder()
                .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value,4, 11))
                .laji(parseString(value, 15, 2))
                .huollonjako(Huollonjako.getEnum(parseCharacter(value, 17)))
                .voimassa(parseCharacter(value, 18).equals("1"))
                .startDate(parseDate(value, 19))
                .endDate(parseDate(value, 27))
                .resolutionDate(parseDate(value, 35))
                .build();
    }

    private static Ammatti parseAmmatti(String value) {
        return Ammatti.builder()
                .ryhmatunnus(Ryhmatunnus.AMMATTI)
                .muutostapa(parseMuutosTapa(value))
                .code(parseString(value, 4, 4))
                .description(parseString(value, 8, 35))
                .build();
    }

    private static SahkopostiOsoite parseSahkopostiOsoite(String value) {
        return SahkopostiOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.SAHKOPOSTIOSOITE)
                .muutostapa(parseMuutosTapa(value))
                .lajikoodi(parseString(value, 4, 2))
                .email(parseString(value, 6, 255))
                .startDate(parseDate(value, 261))
                .endDate(parseDate(value, 269))
                .build();
    }

    private static UlkomainenHenkilonumero parseUlkomainenHenkilonumero(String value) {
        return UlkomainenHenkilonumero.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_HENKILONUMERO)
                .muutostapa(parseMuutosTapa(value))
                .ulkomainenHenkilonumeroId(parseString(value, 4, 30))
                .gender(Gender.getEnum(parseCharacter(value, 34)))
                .countryCode(parseString(value, 35, 3))
                .tietolahde(parseCharacter(value, 38))
                .type(parseCharacter(value, 39))
                .valid(parseCharacter(value, 40).equals("1"))
                .issueDate(parseDate(value, 41))
                .passivointiDate(parseDate(value, 49))
                .saveDateVTJ(parseDate(value, 57))
                .passivointiDateVTJ(parseDate(value, 65))
                .validVTJ(parseCharacter(value, 73).equals("1"))
                .build();
    }

    private static UlkomainenSyntymapaikka parseUlkomainenSyntymapaikka(String value, String additionalInformation) {
        String countryCode = parseString(value, 4, 3);

        UlkomainenSyntymapaikka syntymapaikka = UlkomainenSyntymapaikka.builder()
                .ryhmatunnus(Ryhmatunnus.KUOLINPAIVA)
                .muutostapa(parseMuutosTapa(value))
                .countryCode(countryCode)
                .location(parseString(value, 7, 50))
                .build();

        if (countryCode.equals("998")) {
            syntymapaikka.setAdditionalInformation(parseAdditionalInformation(additionalInformation));
        }

        return syntymapaikka;
    }

    private static Turvakielto parseTurvakielto(String value) {

        LocalDate endDate = null;
        String dateStr = parseString(value, 4, 8);
        /* Toistaiseksi voimassa oleva turvakielto merkitään loppuajalla 99990000. */
        if (!dateStr.equals("99990000")) {
            endDate = parseDate(value, 4);
        }

        return Turvakielto.builder()
                .ryhmatunnus(Ryhmatunnus.TURVAKIELTO)
                .muutostapa(parseMuutosTapa(value))
                .endDate(endDate)
                .build();
    }

    private static UlkomainenOsoite parseUlkomainenOsoite(String value, String additionalInformation) {
        String countryCode = parseString(value, 164, 3);

        UlkomainenOsoite osoite = UlkomainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_OSOITE)
                .muutostapa(parseMuutosTapa(value))
                .streetAddress(parseString(value, 4, 80))
                .municipality(parseString(value, 84, 80))
                .countryCode(countryCode)
                .startDate(parseDate(value, 167))
                .endDate(parseDate(value, 175))
                .build();

        if (countryCode.equals("998")) {
            osoite.setAdditionalInformation(parseAdditionalInformation(additionalInformation));
        }

        return osoite;
    }

    private static TilapainenUlkomainenOsoite parseTilapainenUlkomainenOsoite(String value, String additionalInformation) {
        String countryCode = parseString(value, 164, 3);

        TilapainenUlkomainenOsoite osoite =  TilapainenUlkomainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_OSOITE_TILAPAINEN)
                .muutostapa(parseMuutosTapa(value))
                .streetAddress(parseString(value, 4, 80))
                .municipality(parseString(value, 84, 80))
                .countryCode(countryCode)
                .startDate(parseDate(value, 167))
                .endDate(parseDate(value, 175))
                .build();

        if (countryCode.equals("998")) {
            osoite.setAdditionalInformation(parseAdditionalInformation(additionalInformation));
        }

        return osoite;
    }

    private static Kuolinpaiva parseKuolinpaiva(String value) {
        return Kuolinpaiva.builder()
                .ryhmatunnus(Ryhmatunnus.KUOLINPAIVA)
                .muutostapa(parseMuutosTapa(value))
                .dateOfDeath(parseDate(value, 4))
                .build();
    }

    private static HenkilotunnuksetonHenkilo parseHenkilotunnuksetonHenkilo(String value, String additionalInformation) {
        String nationality = parseString(value, 213, 3);

        HenkilotunnuksetonHenkilo henkilo = HenkilotunnuksetonHenkilo.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILOTUNNUKSETON_HENKILO)
                .muutostapa(parseMuutosTapa(value))
                .dateOfBirth(parseDate(value, 4))
                .gender(Gender.getEnum(parseCharacter(value, 12)))
                .lastname(parseString(value, 13, 100))
                .firstNames(parseString(value, 113, 100))
                .nationality(nationality)
                .build();

        if (nationality.equals("998")) {
            henkilo.setAdditionalInformation(parseAdditionalInformation(additionalInformation));
        }

        return henkilo;
    }

    private static Aidinkieli parseAidinkieli(String value, String additionalInformation) {
        String languageCode = parseString(value, 4,2);

        Aidinkieli aidinkieli = Aidinkieli.builder()
                .ryhmatunnus(Ryhmatunnus.AIDINKIELI)
                .muutostapa(parseMuutosTapa(value))
                .languageCode(languageCode).build();

        if (languageCode.equals("98")) {
            aidinkieli.setAdditionalInformation(parseAdditionalInformation(additionalInformation));
        }

        return aidinkieli;
    }

    private static String parseAdditionalInformation(String value) {
        if (value == null) {
            throw new TietoryhmaParseException("Additional information was null!");
        }
        return value.substring(4).trim();
    }

    private static Muutostapa parseMuutosTapa(String value) {
        Long number = Long.valueOf(value.substring(3, 4));
        return Muutostapa.get(number);
    }

    private static String parseCharacter(String str, int startIndex) {
        return str.substring(startIndex, startIndex+1);
    }

    private static String parseString(String str, int startIndex, int length) {
        try {
            int endIndex = startIndex + length;
            int strLength = str.length();
            if (strLength < endIndex) {
                endIndex = strLength;
            }
            return str.substring(startIndex, endIndex).trim();
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Failed to parse string '{}':", str, e);
            throw e;
        }
    }

    private static LocalDate parseDate(String str, int startIndex) {
        return VRKParseUtil.deserializeDate(parseString(str, startIndex, 8));
    }

}
