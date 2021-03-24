package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.SahkopostiOsoite;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class SahkopostiOsoiteParser implements TietoryhmaParser<SahkopostiOsoite> {

    public SahkopostiOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return SahkopostiOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.SAHKOPOSTIOSOITE)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .lajikoodi(parseString(tietoryhma, 4, 2))
                .email(parseString(tietoryhma, 6, 255))
                .startDate(parseDate(tietoryhma, 261))
                .endDate(parseDate(tietoryhma, 269))
                .build();
    }

    public String serialize(SahkopostiOsoite sahkoposti) {
        return Ryhmatunnus.SAHKOPOSTIOSOITE.getCode()
                + sahkoposti.getMuutostapa().getNumber()
                + serializeString(sahkoposti.getLajikoodi(), 2)
                + serializeString(sahkoposti.getEmail(), 255)
                + serializeDate(sahkoposti.getStartDate())
                + serializeDate(sahkoposti.getEndDate());
    }

}
