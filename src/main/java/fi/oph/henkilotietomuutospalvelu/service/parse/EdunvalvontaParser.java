package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.dto.type.Toimintakelpoisuus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvoja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvonta;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvontaParser implements TietoryhmaParser {

    @Override
    public Edunvalvonta parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Edunvalvonta.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .startDate(parseDate(tietoryhma, 4))
                .endDate(parseDate(tietoryhma, 12))
                .dutiesStarted(parseCharacter(tietoryhma, 20).equals("1"))
                .edunvalvontatieto(Toimintakelpoisuus.getEnum(parseCharacter(tietoryhma, 21)))
                .edunvalvojat(Long.valueOf(parseString(tietoryhma, 22, 2)))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Edunvalvonta edunvalvonta = (Edunvalvonta) tietoryhma;
        return Ryhmatunnus.EDUNVALVONTA.getCode()
                + edunvalvonta.getMuutostapa().getNumber()
                + serializeDate(edunvalvonta.getStartDate())
                + serializeDate(edunvalvonta.getEndDate())
                + (edunvalvonta.getDutiesStarted() ? "1" : " ")
                + edunvalvonta.getEdunvalvontatieto().getCode()
                + serializeString(String.valueOf(edunvalvonta.getEdunvalvojat()), 2);
    }

}
