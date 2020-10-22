package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenSyntymapaikka;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class UlkomainenSyntymapaikkaParser {

    static UlkomainenSyntymapaikka parseUlkomainenSyntymapaikka(String value, String... tarkentavatTietoryhmat) {
        String countryCode = parseString(value, 4, 3);

        UlkomainenSyntymapaikka syntymapaikka = UlkomainenSyntymapaikka.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_SYNTYMAPAIKKA)
                .muutostapa(parseMuutosTapa(value))
                .countryCode(countryCode)
                .location(parseString(value, 7, 50))
                .build();

        if (countryCode.equals("998")) {
            syntymapaikka.setAdditionalInformation(parseAdditionalInformation(tarkentavatTietoryhmat[0]));
        }

        return syntymapaikka;
    }

    static String serializeUlkomainenSyntymapaikka(UlkomainenSyntymapaikka syntymapaikka) {
        String serialized = Ryhmatunnus.ULKOMAINEN_SYNTYMAPAIKKA.getCode()
                + syntymapaikka.getMuutostapa().getNumber()
                + syntymapaikka.getCountryCode()
                + serializeString(syntymapaikka.getLocation(), 50);
        if (syntymapaikka.getAdditionalInformation() != null) {
            serialized = String.join("|", serialized, serializeAdditionalInformation(syntymapaikka.getAdditionalInformation()));
        }
        return serialized;
    }

}
