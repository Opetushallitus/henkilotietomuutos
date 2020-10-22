package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.KotimainenOsoite;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseRyhmatunnus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class KotimainenOsoiteParser {

    static KotimainenOsoite parseKotimainenOsoite(String value) {
        return KotimainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.getEnum(parseRyhmatunnus(value)))
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

    static String serializeKotimainenOsoite(KotimainenOsoite osoite) {
        return osoite.getRyhmatunnus().getCode()
                + osoite.getMuutostapa().getNumber()
                + serializeString(osoite.getLahiosoite(), 100)
                + serializeString(osoite.getLahiosoiteSV(), 100)
                + serializeString(osoite.getKatunumero(), 7)
                + serializeString(osoite.getPorraskirjain(), 1)
                + serializeString(osoite.getHuonenumero(), 3)
                + serializeString(osoite.getJakokirjain(), 1)
                + serializeString(osoite.getPostinumero(), 5)
                + serializeDate(osoite.getStartDate())
                + serializeDate(osoite.getEndDate());
    }

}
