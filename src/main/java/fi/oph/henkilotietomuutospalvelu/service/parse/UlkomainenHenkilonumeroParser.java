package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenHenkilonumero;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class UlkomainenHenkilonumeroParser implements TietoryhmaParser {

    @Override
    public UlkomainenHenkilonumero parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return UlkomainenHenkilonumero.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_HENKILONUMERO)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .ulkomainenHenkilonumeroId(parseString(tietoryhma, 4, 30))
                .gender(Gender.getEnum(parseCharacter(tietoryhma, 34)))
                .countryCode(parseString(tietoryhma, 35, 3))
                .tietolahde(parseCharacter(tietoryhma, 38))
                .type(parseCharacter(tietoryhma, 39))
                .valid(parseCharacter(tietoryhma, 40).equals("1"))
                .issueDate(parseDate(tietoryhma, 41))
                .passivointiDate(parseDate(tietoryhma, 49))
                .saveDateVTJ(parseDate(tietoryhma, 57))
                .passivointiDateVTJ(parseDate(tietoryhma, 65))
                .validVTJ(parseCharacter(tietoryhma, 73).equals("1"))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        UlkomainenHenkilonumero henkilonumero = (UlkomainenHenkilonumero) tietoryhma;
        return Ryhmatunnus.ULKOMAINEN_HENKILONUMERO.getCode()
                + henkilonumero.getMuutostapa().getNumber()
                + serializeString(henkilonumero.getUlkomainenHenkilonumeroId(), 30)
                + serializeString(henkilonumero.getGender().getCode(), 1)
                + serializeString(henkilonumero.getCountryCode(), 3)
                + serializeString(henkilonumero.getTietolahde(), 1)
                + serializeString(henkilonumero.getType(), 1)
                + serializeString(henkilonumero.getValid() ? "1" : "2", 1)
                + serializeDate(henkilonumero.getIssueDate())
                + serializeDate(henkilonumero.getPassivointiDate())
                + serializeDate(henkilonumero.getSaveDateVTJ())
                + serializeDate(henkilonumero.getPassivointiDateVTJ())
                + serializeString(henkilonumero.getValidVTJ() ? "1" : "2", 1);
    }

}
