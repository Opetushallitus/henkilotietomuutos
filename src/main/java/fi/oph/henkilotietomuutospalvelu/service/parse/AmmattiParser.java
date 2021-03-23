package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Ammatti;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class AmmattiParser implements TietoryhmaParser {

    @Override
    public Ammatti parse(String ammatti, String... tarkentavatTietoryhmat) {
        return Ammatti.builder()
                .ryhmatunnus(Ryhmatunnus.AMMATTI)
                .muutostapa(parseMuutosTapa(ammatti))
                .code(parseString(ammatti, 4, 4))
                .description(parseString(ammatti, 8, 35))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Ammatti ammatti = (Ammatti) tietoryhma;
        return Ryhmatunnus.AMMATTI.getCode()
                + ammatti.getMuutostapa().getNumber()
                + serializeString(ammatti.getCode(), 4)
                + serializeString(ammatti.getDescription(), 35);
    }

}
