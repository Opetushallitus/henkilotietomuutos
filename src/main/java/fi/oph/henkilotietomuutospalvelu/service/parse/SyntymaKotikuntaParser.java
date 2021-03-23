package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.SyntymaKotikunta;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;

public class SyntymaKotikuntaParser implements TietoryhmaParser {

    @Override
    public SyntymaKotikunta parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return SyntymaKotikunta.builder()
                .ryhmatunnus(Ryhmatunnus.SYNTYMAKOTIKUNTA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .kuntakoodi(parseString(tietoryhma, 4, 3))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        SyntymaKotikunta syntymaKotikunta = (SyntymaKotikunta) tietoryhma;
        return Ryhmatunnus.SYNTYMAKOTIKUNTA.getCode()
                + syntymaKotikunta.getMuutostapa().getNumber()
                + syntymaKotikunta.getKuntakoodi();
    }

}
