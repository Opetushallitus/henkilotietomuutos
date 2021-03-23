package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.EdunvalvontaValtuutus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvontaValtuutusParser implements TietoryhmaParser {

    @Override
    public EdunvalvontaValtuutus parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return EdunvalvontaValtuutus.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTAVALTUUTUS)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .startDate(parseDate(tietoryhma, 4))
                .endDate(parseDate(tietoryhma, 12))
                .dutiesStarted(parseCharacter(tietoryhma, 20).equals("1"))
                .edunvalvojaValtuutetut(Long.valueOf(parseString(tietoryhma, 21, 2)))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        EdunvalvontaValtuutus valtuutus = (EdunvalvontaValtuutus) tietoryhma;
        return Ryhmatunnus.EDUNVALVONTAVALTUUTUS.getCode()
                + valtuutus.getMuutostapa().getNumber()
                + serializeDate(valtuutus.getStartDate())
                + serializeDate(valtuutus.getEndDate())
                + serializeString(valtuutus.getDutiesStarted() ? "1" : " ", 1)
                + serializeString(String.valueOf(valtuutus.getEdunvalvojaValtuutetut()), 2);
    }

}
