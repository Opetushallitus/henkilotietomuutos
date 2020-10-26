package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.SahkopostiOsoite;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class SahkopostiOsoiteParser {

    static SahkopostiOsoite parseSahkopostiOsoite(String value) {
        return SahkopostiOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.SAHKOPOSTIOSOITE)
                .muutostapa(parseMuutosTapa(value))
                .lajikoodi(parseString(value, 4, 2))
                .email(parseString(value, 6, 255))
                .startDate(parseDate(value, 261))
                .endDate(parseDate(value, 269))
                .build();
    }

    static String serializeSahkopostiOsoite(SahkopostiOsoite sahkoposti) {
        return Ryhmatunnus.SAHKOPOSTIOSOITE.getCode()
                + sahkoposti.getMuutostapa().getNumber()
                + serializeString(sahkoposti.getLajikoodi(), 2)
                + serializeString(sahkoposti.getEmail(), 255)
                + serializeDate(sahkoposti.getStartDate())
                + serializeDate(sahkoposti.getEndDate());
    }

}
