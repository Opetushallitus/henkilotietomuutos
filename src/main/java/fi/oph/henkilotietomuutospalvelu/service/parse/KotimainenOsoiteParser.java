package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.KotimainenOsoite;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseRyhmatunnus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class KotimainenOsoiteParser implements TietoryhmaParser {

    @Override
    public KotimainenOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return KotimainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.getEnum(parseRyhmatunnus(tietoryhma)))
                .muutostapa(parseMuutosTapa(tietoryhma))
                .lahiosoite(parseString(tietoryhma, 4, 100))
                .lahiosoiteSV(parseString(tietoryhma,104, 100))
                .katunumero(parseString(tietoryhma, 204, 7))
                .porraskirjain(parseCharacter(tietoryhma,211))
                .huonenumero(parseString(tietoryhma, 212, 3))
                .jakokirjain(parseCharacter(tietoryhma, 215))
                .postinumero(parseString(tietoryhma, 216, 5))
                .startDate(parseDate(tietoryhma,221))
                .endDate(parseDate(tietoryhma, 229))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        KotimainenOsoite osoite = (KotimainenOsoite) tietoryhma;
        return osoite.getRyhmatunnus().getCode()
                + osoite.getMuutostapa().getNumber()
                + serializeString(osoite.getLahiosoite(), 100)
                + serializeString(osoite.getLahiosoiteSV(), 100)
                + serializeString(osoite.getKatunumero(), 7)
                + serializeString(osoite.getPorraskirjain(), 1)
                + serializeString(osoite.getHuonenumero(), 3)
                + serializeString(osoite.getJakokirjain(), 1)
                + serializeString(osoite.getPostinumero(), 5)
                + serializeDate(osoite.getStartDate())
                + serializeDate(osoite.getEndDate());
    }

}
