package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenOsoite;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseRyhmatunnus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class UlkomainenOsoiteParser {

    static UlkomainenOsoite parseUlkomainenOsoite(String value, String... tarkentavatTietoryhmat) {
        String countryCode = parseString(value, 164, 3);

        UlkomainenOsoite osoite = UlkomainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.getEnum(parseRyhmatunnus(value)))
                .muutostapa(parseMuutosTapa(value))
                .streetAddress(parseString(value, 4, 80))
                .municipality(parseString(value, 84, 80))
                .countryCode(countryCode)
                .startDate(parseDate(value, 167))
                .endDate(parseDate(value, 175))
                .build();

        if (countryCode.equals("998")) {
            osoite.setAdditionalInformation(parseAdditionalInformation(tarkentavatTietoryhmat[0]));
        }

        return osoite;
    }

    static String serializeUlkomainenOsoite(UlkomainenOsoite osoite) {
        String serialized = osoite.getRyhmatunnus().getCode()
                + osoite.getMuutostapa().getNumber()
                + serializeString(osoite.getStreetAddress(), 80)
                + serializeString(osoite.getMunicipality(), 80)
                + osoite.getCountryCode()
                + serializeDate(osoite.getStartDate())
                + serializeDate(osoite.getEndDate());
        if (osoite.getCountryCode().equals("998")) {
            serialized = String.join("|", serialized, serializeAdditionalInformation(osoite.getAdditionalInformation()));
        }
        return serialized;
    }

}
