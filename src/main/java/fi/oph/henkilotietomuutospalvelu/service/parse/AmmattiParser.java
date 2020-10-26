package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Ammatti;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class AmmattiParser {

    static Ammatti parseAmmatti(String value) {
        return Ammatti.builder()
                .ryhmatunnus(Ryhmatunnus.AMMATTI)
                .muutostapa(parseMuutosTapa(value))
                .code(parseString(value, 4, 4))
                .description(parseString(value, 8, 35))
                .build();
    }

    static String serializeAmmatti(Ammatti ammatti) {
        return Ryhmatunnus.AMMATTI.getCode()
                + ammatti.getMuutostapa().getNumber()
                + serializeString(ammatti.getCode(), 4)
                + serializeString(ammatti.getDescription(), 35);
    }

}
