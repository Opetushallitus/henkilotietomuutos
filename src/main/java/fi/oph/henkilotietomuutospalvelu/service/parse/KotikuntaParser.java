package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kotikunta;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class KotikuntaParser implements TietoryhmaParser<Kotikunta> {

    public Kotikunta parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Kotikunta.builder()
                .ryhmatunnus(Ryhmatunnus.KOTIKUNTA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .code(parseString(tietoryhma, 4, 3))
                .moveDate(parseDate(tietoryhma, 7))
                .build();
    }

    public String serialize(Kotikunta kunta) {
        return Ryhmatunnus.KOTIKUNTA.getCode()
                + kunta.getMuutostapa().getNumber()
                + serializeString(kunta.getCode(), 3)
                + serializeDate(kunta.getMoveDate());
    }

}
