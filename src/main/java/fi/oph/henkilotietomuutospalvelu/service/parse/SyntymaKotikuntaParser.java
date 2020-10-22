package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.SyntymaKotikunta;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;

public class SyntymaKotikuntaParser {

    static SyntymaKotikunta parseSyntymaKotikunta(String value) {
        return SyntymaKotikunta.builder()
                .ryhmatunnus(Ryhmatunnus.SYNTYMAKOTIKUNTA)
                .muutostapa(parseMuutosTapa(value))
                .kuntakoodi(parseString(value, 4, 3))
                .build();
    }

    static String serializeSyntymaKotikunta(SyntymaKotikunta syntymaKotikunta) {
        return Ryhmatunnus.SYNTYMAKOTIKUNTA.getCode()
                + syntymaKotikunta.getMuutostapa().getNumber()
                + syntymaKotikunta.getKuntakoodi();
    }

}
