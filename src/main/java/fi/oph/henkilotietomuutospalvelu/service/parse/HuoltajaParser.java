package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Oikeus;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseHenkilotunnuksetonHenkilo;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseRyhmatunnus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeHenkilotunnuksetonHenkilo;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HuoltajaParser {

    static Huoltaja parseHuoltaja(String value, String... tarkentavatTietoryhmat) {
        if (value.length() == 43) {
            // formaatti ennen 2019-12-01
            return Huoltaja.builder()
                    .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                    .muutostapa(parseMuutosTapa(value))
                    .hetu(parseString(value,4, 11))
                    //.laji(parseString(value, 15, 2)) tiedon sisältö muuttunut uudessa versiossa
                    //.huollonjako(Huollonjako.getEnum(parseCharacter(value, 17))) tietoa ei enää uudessa formaatissa
                    //.voimassa(parseCharacter(value, 18).equals("1")) tietoa ei enää uudessa formaatissa
                    .startDate(parseDate(value, 19))
                    .endDate(parseDate(value, 27))
                    //.resolutionDate(parseDate(value, 35)) // tietoa ei enää uudessa formaatissa
                    .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                    .build();
        }
        return Huoltaja.builder()
                .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .laji(parseString(value, 15, 1))
                .rooli(parseString(value, 16, 1))
                .startDate(parseDate(value, 17))
                .endDate(parseDate(value, 25))
                .asuminen(parseString(value, 33, 1))
                .asuminenAlkupvm(parseDate(value, 34))
                .asuminenLoppupvm(parseDate(value, 42))
                .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                .oikeudet(parseOikeudet(tarkentavatTietoryhmat))
                .build();
    }

    // formaatti ennen 2019-12-01 ei tuettu!
    static String serializeHuoltaja(Huoltaja huoltaja) {
        String serialized = Ryhmatunnus.HUOLTAJA.getCode()
                + huoltaja.getMuutostapa().getNumber()
                + serializeString(huoltaja.getHetu(), 11)
                + serializeString(huoltaja.getLaji(), 1)
                + serializeString(huoltaja.getRooli(), 1)
                + serializeDate(huoltaja.getStartDate())
                + serializeDate(huoltaja.getEndDate())
                + serializeString(huoltaja.getAsuminen(), 1)
                + serializeDate(huoltaja.getAsuminenAlkupvm())
                + serializeDate(huoltaja.getAsuminenLoppupvm());
        if (huoltaja.getHenkilotunnuksetonHenkilo() != null) {
            serialized = String.join("|", serialized,
                    serializeHenkilotunnuksetonHenkilo(huoltaja.getHenkilotunnuksetonHenkilo()));
        }
        serialized = String.join("|", serialized, huoltaja.getOikeudet().stream().map(
                HuoltajaParser::serializeOikeus
        ).collect(Collectors.joining("|")));
        return serialized;
    }

    private static Set<Oikeus> parseOikeudet(String... tietoryhmat) {
        return Arrays.stream(tietoryhmat)
                .filter(tietoryhma -> "320".equals(parseRyhmatunnus(tietoryhma)))
                .map(HuoltajaParser::parseOikeus)
                .collect(Collectors.toSet());
    }

    private static Oikeus parseOikeus(String tietoryhmaStr) {
        return Oikeus.builder()
                .ryhmatunnus(Ryhmatunnus.OIKEUS)
                .muutostapa(parseMuutosTapa(tietoryhmaStr))
                .koodi(parseString(tietoryhmaStr, 4, 4))
                .alkupvm(parseDate(tietoryhmaStr, 8))
                .loppupvm(parseDate(tietoryhmaStr, 16))
                .build();
    }

    private static String serializeOikeus(Oikeus oikeus) {
        return Ryhmatunnus.OIKEUS.getCode()
                + oikeus.getMuutostapa().getNumber()
                + serializeString(oikeus.getKoodi(), 4)
                + serializeDate(oikeus.getAlkupvm())
                + serializeDate(oikeus.getLoppupvm());
    }

}
