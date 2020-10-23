package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kotikunta;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class KotikuntaParser {

    static Kotikunta parseKotikunta(String value) {
        return Kotikunta.builder()
                .ryhmatunnus(Ryhmatunnus.KOTIKUNTA)
                .muutostapa(parseMuutosTapa(value))
                .code(parseString(value, 4, 3))
                .moveDate(parseDate(value, 7))
                .build();
    }

    static String serializeKotikunta(Kotikunta kunta) {
        return Ryhmatunnus.KOTIKUNTA.getCode()
                + kunta.getMuutostapa().getNumber()
                + serializeString(kunta.getCode(), 3)
                + serializeDate(kunta.getMoveDate());
    }

}
