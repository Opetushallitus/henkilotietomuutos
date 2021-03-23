package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Postiosoite;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class PostiosoiteParser implements TietoryhmaParser<Postiosoite> {

    public static final PostiosoiteParser INSTANCE = new PostiosoiteParser();

    public Postiosoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Postiosoite.builder()
                .ryhmatunnus(Ryhmatunnus.POSTIOSOITE)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .postiosoite(parseString(tietoryhma, 4, 50))
                .postiosoiteSv(parseString(tietoryhma, 54, 50))
                .postinumero(parseString(tietoryhma, 104, 5))
                .startDate(parseDate(tietoryhma, 109))
                .endDate(parseDate(tietoryhma, 117))
                .build();
    }

    public String serialize(Postiosoite postiosoite) {
        return Ryhmatunnus.POSTIOSOITE.getCode()
                + postiosoite.getMuutostapa().getNumber()
                + serializeString(postiosoite.getPostiosoite(), 50)
                + serializeString(postiosoite.getPostiosoiteSv(), 50)
                + serializeString(postiosoite.getPostinumero(), 5)
                + serializeDate(postiosoite.getStartDate())
                + serializeDate(postiosoite.getEndDate());
    }

}
