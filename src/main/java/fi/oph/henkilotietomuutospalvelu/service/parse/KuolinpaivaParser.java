package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kuolinpaiva;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;

public class KuolinpaivaParser {

    static Kuolinpaiva parseKuolinpaiva(String value) {
        return Kuolinpaiva.builder()
                .ryhmatunnus(Ryhmatunnus.KUOLINPAIVA)
                .muutostapa(parseMuutosTapa(value))
                .dateOfDeath(parseDate(value, 4))
                .build();
    }

    static String serializeKuolinpaiva(Kuolinpaiva kuolinpaiva) {
        return Ryhmatunnus.KUOLINPAIVA.getCode()
                + kuolinpaiva.getMuutostapa().getNumber()
                + serializeDate(kuolinpaiva.getDateOfDeath());
    }
}
