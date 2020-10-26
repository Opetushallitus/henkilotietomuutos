package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvoja;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseHenkilotunnuksetonHenkilo;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeHenkilotunnuksetonHenkilo;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvojaParser {

    static Edunvalvoja parseEdunvalvoja(String value, String... tarkentavatTietoryhmat) {
        return Edunvalvoja.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVOJA)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .yTunnus(parseString(value, 15, 9))
                .municipalityCode(parseString(value, 24, 3))
                .oikeusaputoimistoKoodi(parseString(value, 27, 6))
                .startDate(parseDate(value, 33))
                .endDate(parseDate(value, 41))
                .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                .build();
    }

    static String serializeEdunvalvoja(Edunvalvoja edunvalvoja) {
        String serialized = Ryhmatunnus.EDUNVALVOJA.getCode()
                + edunvalvoja.getMuutostapa().getNumber()
                + serializeString(edunvalvoja.getHetu(), 11)
                + serializeString(edunvalvoja.getYTunnus(), 9)
                + serializeString(edunvalvoja.getMunicipalityCode(), 3)
                + serializeString(edunvalvoja.getOikeusaputoimistoKoodi(), 6)
                + serializeDate(edunvalvoja.getStartDate())
                + serializeDate(edunvalvoja.getEndDate());
        if (edunvalvoja.getHenkilotunnuksetonHenkilo() != null) {
            serialized = String.join("|", serialized,
                    serializeHenkilotunnuksetonHenkilo(edunvalvoja.getHenkilotunnuksetonHenkilo()));
        }
        return serialized;
    }

}
