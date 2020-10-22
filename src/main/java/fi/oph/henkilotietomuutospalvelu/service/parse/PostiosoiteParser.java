package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Postiosoite;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class PostiosoiteParser {

    static Postiosoite parsePostiosoite(String value) {
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

    static String serializePostiosoite(Postiosoite postiosoite) {
        return Ryhmatunnus.POSTIOSOITE.getCode()
                + postiosoite.getMuutostapa().getNumber()
                + serializeString(postiosoite.getPostiosoite(), 50)
                + serializeString(postiosoite.getPostiosoiteSv(), 50)
                + serializeString(postiosoite.getPostinumero(), 5)
                + serializeDate(postiosoite.getStartDate())
                + serializeDate(postiosoite.getEndDate());
    }

}
