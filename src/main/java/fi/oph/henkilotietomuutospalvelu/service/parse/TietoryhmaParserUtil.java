package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.service.exception.TietoryhmaParseException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import static fi.oph.henkilotietomuutospalvelu.service.parse.AidinkieliParser.parseAidinkieli;
import static fi.oph.henkilotietomuutospalvelu.service.parse.AmmattiParser.parseAmmatti;
import static fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvojaParser.parseEdunvalvoja;
import static fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvontaParser.parseEdunvalvonta;
import static fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvontaValtuutettuParser.parseEdunvalvontaValtuutettu;
import static fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvontaValtuutusParser.parseEdunvalvontaValtuutus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.HenkiloNameChangeParser.parseHenkiloNameChange;
import static fi.oph.henkilotietomuutospalvelu.service.parse.HenkiloNameParser.parseHenkiloName;
import static fi.oph.henkilotietomuutospalvelu.service.parse.HenkilotunnuskorjausParser.parseHenkilotunnuskorjaus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.HuoltajaParser.parseHuoltaja;
import static fi.oph.henkilotietomuutospalvelu.service.parse.KansalaisuusParser.parseKansalaisuus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.KotikuntaParser.parseKotikunta;
import static fi.oph.henkilotietomuutospalvelu.service.parse.KotimainenOsoiteParser.parseKotimainenOsoite;
import static fi.oph.henkilotietomuutospalvelu.service.parse.KuolinpaivaParser.parseKuolinpaiva;
import static fi.oph.henkilotietomuutospalvelu.service.parse.KutsumanimiParser.parseKutsumanimi;
import static fi.oph.henkilotietomuutospalvelu.service.parse.PostiosoiteParser.parsePostiosoite;
import static fi.oph.henkilotietomuutospalvelu.service.parse.SahkopostiOsoiteParser.parseSahkopostiOsoite;
import static fi.oph.henkilotietomuutospalvelu.service.parse.SukupuoliParser.parseSukupuoli;
import static fi.oph.henkilotietomuutospalvelu.service.parse.SyntymaKotikuntaParser.parseSyntymaKotikunta;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TurvakieltoParser.parseTurvakielto;
import static fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenHenkilonumeroParser.parseUlkomainenHenkilonumero;
import static fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenOsoiteParser.parseUlkomainenOsoite;
import static fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenSyntymapaikkaParser.parseUlkomainenSyntymapaikka;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

@Slf4j
public class TietoryhmaParserUtil {

    public static Tietoryhma deserializeTietoryhma(String tietoryhma, String... tarkentavatTietoryhmat)
            throws TietoryhmaParseException {
        if (tietoryhma.length() < 4) {
            throw new TietoryhmaParseException("Tietoryhmä has a length less than 4 and is not valid!");
        }
        try {
            String ryhmatunnus = parseRyhmatunnus(tietoryhma);
            switch (ryhmatunnus) {
                case ("452"): // 4.4.2 Selväkielinen tieto
                case ("451"): // Henkilötunnukseton henkilö
                    // Skip, as we already read these in the previous tietoryhma.
                    return null;

                case ("001"):
                    return parseHenkilotunnuskorjaus(tietoryhma);
                case ("002"):
                    return parseAidinkieli(tietoryhma, tarkentavatTietoryhmat);
                case ("003"):
                    return parseSukupuoli(tietoryhma);
                case ("004"):
                    return parseHenkiloName(tietoryhma);
                case ("005"):
                    return parseHenkiloNameChange(tietoryhma);
                case ("007"):
                    return parseKansalaisuus(tietoryhma, tarkentavatTietoryhmat);
                case ("008"):
                    return parseSyntymaKotikunta(tietoryhma);
                case ("009"):
                    return parseUlkomainenSyntymapaikka(tietoryhma, tarkentavatTietoryhmat);
                case ("013"):
                    return parseKuolinpaiva(tietoryhma);
                case ("015"):
                    return parseTurvakielto(tietoryhma);
                case ("101"):
                    return parseKotimainenOsoite(tietoryhma);
                case ("102"):
                    return TilapainenKotimainenOsoite.from(parseKotimainenOsoite(tietoryhma));
                case ("103"):
                    return parsePostiosoite(tietoryhma);
                case ("104"):
                    return parseUlkomainenOsoite(tietoryhma, tarkentavatTietoryhmat);
                case ("105"):
                    return TilapainenUlkomainenOsoite.from(parseUlkomainenOsoite(tietoryhma, tarkentavatTietoryhmat));
                case ("204"):
                    return parseKotikunta(tietoryhma);
                case ("305"):
                    return parseHuoltaja(tietoryhma, tarkentavatTietoryhmat);
                case ("306"):
                    return parseEdunvalvonta(tietoryhma);
                case ("307"):
                    return parseEdunvalvoja(tietoryhma, tarkentavatTietoryhmat);
                case ("316"):
                    return parseEdunvalvontaValtuutus(tietoryhma);
                case ("317"):
                    return parseEdunvalvontaValtuutettu(tietoryhma, tarkentavatTietoryhmat);
                case ("401"):
                    return parseAmmatti(tietoryhma);
                case ("421"):
                    return parseSahkopostiOsoite(tietoryhma);
                case ("422"):
                    return parseUlkomainenHenkilonumero(tietoryhma);
                case ("423"):
                    return parseKutsumanimi(tietoryhma);

                case ("006"): // 3.1.6 Nimenmuutostapa (välitetään vain seurakunnille)
                case ("010"): // 3.1.10 Siviilisääty
                case ("011"): // 3.1.11 Nykyinen paikallinen rekisteriviranomainen
                case ("012"): // 3.1.12 Historiaa paikallisista rekisteriviranomaisista
                case ("014"): // 3.1.14 Kuolleeksijulistamispäivä
                case ("016"): // 3.1.16 Muut tietojenluovutuskiellot (välitetään vain seurakunnille)
                case ("201"): // 3.3.1 Vakituinen kotipaikkatunnus. Tietoryhmä poistuu pl. kunnat
                case ("205"): // 3.3.3 Edellinen kotikunta
                case ("206"): // 3.3.4 Tilapäinen kunta
                case ("208"): // 3.3.5 Vuodenvaihteen kunta
                case ("209"): // 3.3.6 Poissaolo
                case ("210"): // 3.3.7 Suomeen muuttopäivä
                case ("211"): // 3.3.8 Valtio, josta muuttanut
                case ("212"): // 3.3.9 Valtio, johon muuttanut
                case ("213"): // 3.3.10 Väestökirjanpitokunta
                case ("241"): // 3.3.11 Vakinainen asuminen Suomessa
                case ("242"): // 3.3.12 Edellinen vakinainen asuminen Suomessa
                case ("243"): // 3.3.13 Tilapäinen asuminen Suomessa
                case ("251"): // 3.3.14 Ulkomailla ja ns. 900-ryhmissä olevien vakinainen asuminen
                case ("252"): // 3.3.15 Edellinen ulkomailla ja ns. 900-ryhmissä olevien vakinainen asuminen
                case ("253"): // 3.3.16 Tilapäinen ulkomailla ja ns. 900-ryhmissä olevien asuminen
                case ("301"): // 4.1.1 Lapsi (päähenkilönä vanhempi)
                case ("302"): // 4.1.2 Vanhempi (päähenkilönä lapsi)
                case ("303"): // 4.1.3 Ottolapsisuhde
                case ("304"): // 4.1.4 Huollettava (päähenkilönä huoltaja)
                case ("308"): // 4.1.8 Huostaanotto
                case ("309"): // 4.1.9 Perhesuhde (vain perustietona)
                case ("351"): // 4.2.1 Avioliitto
                case ("352"): // 4.2.2 Järjestysnumero ja vihkitapa
                case ("353"): // 4.2.3 Päättynyt avioliitto
                case ("354"): // 4.2.4 Asumusero
                case ("355"): // 4.2.5 Rekisteröity parisuhde
                case ("402"): // 4.3.2 Uskontokunta
                case ("403"): // 4.3.3 Historiaa uskontokunnasta
                case ("405"): // 4.3.4 Järjestyskirjain
                case ("407"): // 4.3.5 Uskontokuntalaji
                case ("410"): // 4.3.6 Asiointikieli
                case ("420"): // 4.3.7 Sähköinen asiointitunnus
                case ("424"): // 4.3.11 Kotimainen yhteysosoite
                case ("425"): // 4.3.12 Ulkomainen yhteysosoite
                case ("453"): // 4.4.3 Tarkentava nimi (välitetään vain lisäpalveluna)

                    // Lomakkeet
                case ("471"): // 4.5.1 XAA:lta välitettävät tiedot
                case ("472"): // 4.5.2 XBL:ltä välitettävät tiedot
                case ("473"): // 4.5.3 XBO:lta välitettävät tiedot
                case ("474"): // 4.5.4 XBV:ltä ja XPV:ltä välitettävät tiedot
                case ("902"): // 6.1 Ortod. kirkolliset tiedot
                default:
                    throw new TietoryhmaParseException("Unsupported Tietoryhma! Ryhmatunnus: " + ryhmatunnus);
            }
        } catch (RuntimeException e) {
            if (e instanceof TietoryhmaParseException) {
                throw e;
            }
            throw new TietoryhmaParseException("Deserializing Tietoryhma failed!", e);
        }
    }

    @NotNull
    public static String parseRyhmatunnus(String tietoryhma) {
        return parseString(tietoryhma, 0, 3);
    }
    
    static HenkilotunnuksetonHenkilo parseHenkilotunnuksetonHenkilo(String... tietoryhmat) {
        HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo = null;
        for (String tietoryhma : tietoryhmat) {
            String ryhmatunnus = parseRyhmatunnus(tietoryhma);
            switch (ryhmatunnus) {
                case "451":
                    henkilotunnuksetonHenkilo = parseHenkilotunnuksetonHenkilo(tietoryhma);
                    break;
                case "452":
                    if (henkilotunnuksetonHenkilo != null && "998".equals(henkilotunnuksetonHenkilo.getNationality())) {
                        henkilotunnuksetonHenkilo.setAdditionalInformation(parseAdditionalInformation(tietoryhma));
                    }
                    break;
            }
        }
        return henkilotunnuksetonHenkilo;
    }

    private static HenkilotunnuksetonHenkilo parseHenkilotunnuksetonHenkilo(String value) {
        return HenkilotunnuksetonHenkilo.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILOTUNNUKSETON_HENKILO)
                .muutostapa(parseMuutosTapa(value))
                .dateOfBirth(parseDate(value, 4))
                .gender(Gender.getEnum(parseCharacter(value, 12)))
                .lastname(parseString(value, 13, 100))
                .firstNames(parseString(value, 113, 100))
                .nationality(parseString(value, 213, 3))
                .build();
    }

    static String serializeHenkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo henkilo) {
        String serialized = Ryhmatunnus.HENKILOTUNNUKSETON_HENKILO.getCode()
                + henkilo.getMuutostapa().getNumber()
                + serializeDate(henkilo.getDateOfBirth())
                + henkilo.getGender().getCode()
                + serializeString(henkilo.getLastname(), 100)
                + serializeString(henkilo.getFirstNames(), 100)
                + serializeString(henkilo.getNationality(), 3);
        if ("998".equals(henkilo.getNationality())) {
            serialized = String.join("|", serialized,
                    serializeAdditionalInformation(henkilo.getAdditionalInformation()));
        }
        return serialized;
    }

    static String parseAdditionalInformation(String value) {
        if (value == null) {
            throw new TietoryhmaParseException("Additional information was null!");
        }
        return value.substring(4).trim();
    }

    static String serializeAdditionalInformation(String information) {
        return Ryhmatunnus.LISATIETO.getCode()
                + Muutostapa.LISATIETO.getNumber()
                + serializeString(information, 30);
    }

    static Muutostapa parseMuutosTapa(String value) {
        Long number = Long.valueOf(value.substring(3, 4));
        return Muutostapa.get(number);
    }

    static String parseCharacter(String str, int startIndex) {
        return str.substring(startIndex, startIndex+1);
    }

    static String parseString(String str, int startIndex, int length) {
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

    static LocalDate parseDate(String str, int startIndex) {
        return VRKParseUtil.deserializeDate(parseString(str, startIndex, 8));
    }

}
