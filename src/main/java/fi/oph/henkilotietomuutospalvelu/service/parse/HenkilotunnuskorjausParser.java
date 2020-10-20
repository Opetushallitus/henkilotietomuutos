package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;

public class HenkilotunnuskorjausParser {

    static Henkilotunnuskorjaus parseHenkilotunnuskorjaus(String value) {
        return Henkilotunnuskorjaus.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILOTUNNUS_KORJAUS)
                .muutostapa(parseMuutosTapa(value))
                .hetu(parseString(value, 4, 11))
                .active(parseCharacter(value, 15).equals("1")).build();
    }

    static String serializeHenkilotunnuskorjaus(Henkilotunnuskorjaus korjaus) {
        return Ryhmatunnus.HENKILOTUNNUS_KORJAUS.getCode()
                + korjaus.getMuutostapa().getNumber()
                + korjaus.getHetu()
                + (korjaus.getActive() ? 1 : 0);
    }

}
