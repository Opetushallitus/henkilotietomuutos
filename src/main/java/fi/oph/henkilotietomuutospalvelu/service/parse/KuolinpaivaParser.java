package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kuolinpaiva;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;

public class KuolinpaivaParser implements TietoryhmaParser<Kuolinpaiva> {

    public Kuolinpaiva parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Kuolinpaiva.builder()
                .ryhmatunnus(Ryhmatunnus.KUOLINPAIVA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .dateOfDeath(parseDate(tietoryhma, 4))
                .build();
    }

    public String serialize(Kuolinpaiva kuolinpaiva) {
        return Ryhmatunnus.KUOLINPAIVA.getCode()
                + kuolinpaiva.getMuutostapa().getNumber()
                + serializeDate(kuolinpaiva.getDateOfDeath());
    }
}
