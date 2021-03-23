package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kutsumanimi;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class KutsumanimiParser implements TietoryhmaParser {

    @Override
    public Kutsumanimi parse(String value, String... tarkentavatTietoryhmat) {
        return Kutsumanimi.builder()
                .ryhmatunnus(Ryhmatunnus.KUTSUMANIMI)
                .muutostapa(parseMuutosTapa(value))
                .name(parseString(value, 4,100))
                .type(parseString(value, 104,2 ))
                .startDate(parseDate(value, 106))
                .endDate(parseDate(value, 114))
                .nonStandardCharacters(parseCharacter(value, 122).equals("1"))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Kutsumanimi nimi = (Kutsumanimi) tietoryhma;
        return Ryhmatunnus.KUTSUMANIMI.getCode()
                + nimi.getMuutostapa().getNumber()
                + serializeString(nimi.getName(), 100)
                + serializeString(nimi.getType(), 2)
                + serializeDate(nimi.getStartDate())
                + serializeDate(nimi.getEndDate())
                + serializeString(nimi.getNonStandardCharacters() ? "1" : " ", 1);
    }

}
