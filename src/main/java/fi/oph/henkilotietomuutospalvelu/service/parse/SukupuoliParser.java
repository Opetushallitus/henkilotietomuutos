package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Sukupuoli;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;

public class SukupuoliParser {

    static Sukupuoli parseSukupuoli(String value) {
        return Sukupuoli.builder()
                .ryhmatunnus(Ryhmatunnus.SUKUPUOLI)
                .muutostapa(parseMuutosTapa(value))
                .gender(Gender.getEnum(parseCharacter(value, 4)))
                .build();
    }

    static String serializeSukupuoli(Sukupuoli sukupuoli) {
        return Ryhmatunnus.SUKUPUOLI.getCode()
                + sukupuoli.getMuutostapa().getNumber()
                + sukupuoli.getGender().getCode();
    }

}
