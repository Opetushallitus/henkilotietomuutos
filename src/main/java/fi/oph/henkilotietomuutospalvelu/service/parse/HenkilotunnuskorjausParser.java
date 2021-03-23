package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;

public class HenkilotunnuskorjausParser implements TietoryhmaParser {

    @Override
    public Henkilotunnuskorjaus parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Henkilotunnuskorjaus.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILOTUNNUS_KORJAUS)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .hetu(parseString(tietoryhma, 4, 11))
                .active(parseCharacter(tietoryhma, 15).equals("1")).build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Henkilotunnuskorjaus korjaus = (Henkilotunnuskorjaus) tietoryhma;
        return Ryhmatunnus.HENKILOTUNNUS_KORJAUS.getCode()
                + korjaus.getMuutostapa().getNumber()
                + korjaus.getHetu()
                + (korjaus.getActive() ? 1 : 0);
    }

}
