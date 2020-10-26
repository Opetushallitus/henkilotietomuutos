package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenHenkilonumero;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class UlkomainenHenkilonumeroParser {

    static UlkomainenHenkilonumero parseUlkomainenHenkilonumero(String value) {
        return UlkomainenHenkilonumero.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_HENKILONUMERO)
                .muutostapa(parseMuutosTapa(value))
                .ulkomainenHenkilonumeroId(parseString(value, 4, 30))
                .gender(Gender.getEnum(parseCharacter(value, 34)))
                .countryCode(parseString(value, 35, 3))
                .tietolahde(parseCharacter(value, 38))
                .type(parseCharacter(value, 39))
                .valid(parseCharacter(value, 40).equals("1"))
                .issueDate(parseDate(value, 41))
                .passivointiDate(parseDate(value, 49))
                .saveDateVTJ(parseDate(value, 57))
                .passivointiDateVTJ(parseDate(value, 65))
                .validVTJ(parseCharacter(value, 73).equals("1"))
                .build();
    }

    static String serializeUlkomainenHenkilonumero(UlkomainenHenkilonumero henkilonumero) {
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
