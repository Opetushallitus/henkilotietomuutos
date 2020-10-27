package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.EdunvalvontaValtuutus;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvontaValtuutusParser {

    static EdunvalvontaValtuutus parseEdunvalvontaValtuutus(String value) {
        return EdunvalvontaValtuutus.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTAVALTUUTUS)
                .muutostapa(parseMuutosTapa(value))
                .startDate(parseDate(value, 4))
                .endDate(parseDate(value, 12))
                .dutiesStarted(parseCharacter(value, 20).equals("1"))
                .edunvalvojaValtuutetut(Long.valueOf(parseString(value, 21, 2)))
                .build();
    }

    static String serializeEdunvalvontaValtuutus(EdunvalvontaValtuutus valtuutus) {
        return Ryhmatunnus.EDUNVALVONTAVALTUUTUS.getCode()
                + valtuutus.getMuutostapa().getNumber()
                + serializeDate(valtuutus.getStartDate())
                + serializeDate(valtuutus.getEndDate())
                + serializeString(valtuutus.getDutiesStarted() ? "1" : " ", 1)
                + serializeString(String.valueOf(valtuutus.getEdunvalvojaValtuutetut()), 2);
    }

}
