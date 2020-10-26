package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.EdunvalvontaValtuutettu;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseHenkilotunnuksetonHenkilo;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeHenkilotunnuksetonHenkilo;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvontaValtuutettuParser {

    static EdunvalvontaValtuutettu parseEdunvalvontaValtuutettu(String value, String... tarkentavatTietoryhmat) {
        return EdunvalvontaValtuutettu.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTAVALTUUTETTU)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .startDate(parseDate(value, 15))
                .endDate(parseDate(value, 23))
                .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                .build();
    }

    static String serializeEdunvalvontaValtuutettu(EdunvalvontaValtuutettu valtuutettu) {
        String serialized = Ryhmatunnus.EDUNVALVONTAVALTUUTETTU.getCode()
                + valtuutettu.getMuutostapa().getNumber()
                + serializeString(valtuutettu.getHetu(), 11)
                + serializeDate(valtuutettu.getStartDate())
                + serializeDate(valtuutettu.getEndDate());
        if (valtuutettu.getHenkilotunnuksetonHenkilo() != null) {
            serialized = String.join("|", serialized,
                    serializeHenkilotunnuksetonHenkilo(valtuutettu.getHenkilotunnuksetonHenkilo()));
        }
        return serialized;
    }

}
