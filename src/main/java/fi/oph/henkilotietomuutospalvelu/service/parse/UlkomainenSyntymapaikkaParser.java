package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenSyntymapaikka;
import lombok.extern.slf4j.Slf4j;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

@Slf4j
public class UlkomainenSyntymapaikkaParser implements TietoryhmaParser<UlkomainenSyntymapaikka> {

    public UlkomainenSyntymapaikka parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        String countryCode = parseString(tietoryhma, 4, 3);

        UlkomainenSyntymapaikka syntymapaikka = UlkomainenSyntymapaikka.builder()
                .ryhmatunnus(Ryhmatunnus.ULKOMAINEN_SYNTYMAPAIKKA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .countryCode(countryCode)
                .location(parseString(tietoryhma, 7, 50))
                .build();
        if (countryCode.equals("998")) {
            if (tarkentavatTietoryhmat.length > 0) {
                syntymapaikka.setAdditionalInformation(parseAdditionalInformation(tarkentavatTietoryhmat[0]));
            } else {
                log.warn("Missing foreign birth place additional information!");
            }
        }

        return syntymapaikka;
    }

    public String serialize(UlkomainenSyntymapaikka syntymapaikka) {
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
