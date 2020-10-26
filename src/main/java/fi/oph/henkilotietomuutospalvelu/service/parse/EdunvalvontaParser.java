package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.dto.type.Toimintakelpoisuus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvonta;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvontaParser {

    static Edunvalvonta parseEdunvalvonta(String value) {
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

    static String serializeEdunvalvonta(Edunvalvonta edunvalvonta) {
        return Ryhmatunnus.EDUNVALVONTA.getCode()
                + edunvalvonta.getMuutostapa().getNumber()
                + serializeDate(edunvalvonta.getStartDate())
                + serializeDate(edunvalvonta.getEndDate())
                + (edunvalvonta.getDutiesStarted() ? "1" : " ")
                + edunvalvonta.getEdunvalvontatieto().getCode()
                + serializeString(String.valueOf(edunvalvonta.getEdunvalvojat()), 2);
    }

}
