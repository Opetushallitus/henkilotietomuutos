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

public class UlkomainenOsoiteParser implements TietoryhmaParser<UlkomainenOsoite> {

    public static final UlkomainenOsoiteParser INSTANCE = new UlkomainenOsoiteParser();

    public UlkomainenOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        String countryCode = parseString(tietoryhma, 164, 3);

        UlkomainenOsoite osoite = UlkomainenOsoite.builder()
                .ryhmatunnus(Ryhmatunnus.getEnum(parseRyhmatunnus(tietoryhma)))
                .muutostapa(parseMuutosTapa(tietoryhma))
                .streetAddress(parseString(tietoryhma, 4, 80))
                .municipality(parseString(tietoryhma, 84, 80))
                .countryCode(countryCode)
                .startDate(parseDate(tietoryhma, 167))
                .endDate(parseDate(tietoryhma, 175))
                .build();

        if (countryCode.equals("998")) {
            if (tarkentavatTietoryhmat.length > 0) {
                osoite.setAdditionalInformation(parseAdditionalInformation(tarkentavatTietoryhmat[0]));
            }
        }

        return osoite;
    }

    public String serialize(UlkomainenOsoite osoite) {
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
