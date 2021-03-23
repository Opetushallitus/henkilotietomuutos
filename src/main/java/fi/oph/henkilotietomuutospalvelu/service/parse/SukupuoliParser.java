package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Sukupuoli;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;

public class SukupuoliParser implements TietoryhmaParser {

    @Override
    public Sukupuoli parse(String tietoryhma, String... tarkentavatTietoryhma) {
        return Sukupuoli.builder()
                .ryhmatunnus(Ryhmatunnus.SUKUPUOLI)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .gender(Gender.getEnum(parseCharacter(tietoryhma, 4)))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Sukupuoli sukupuoli = (Sukupuoli) tietoryhma;
        return Ryhmatunnus.SUKUPUOLI.getCode()
                + sukupuoli.getMuutostapa().getNumber()
                + sukupuoli.getGender().getCode();
    }

}
