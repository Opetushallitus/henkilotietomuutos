package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kansalaisuus;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class KansalaisuusParser {

    static Kansalaisuus parseKansalaisuus(String value, String... tarkentavatTietoryhmat) {
        String code = parseString(value, 4, 3);
        if (code.equals("998")) {
            code = parseAdditionalInformation(tarkentavatTietoryhmat[0]);
        }

        return Kansalaisuus.builder()
                .ryhmatunnus(Ryhmatunnus.KANSALAISUUS)
                .muutostapa(parseMuutosTapa(value))
                .code(code)
                .valid(parseCharacter(value, 7).equals("1"))
                .startDate(parseDate(value, 8))
                .endDate(parseDate(value,16))
                .build();
    }

    static String serializeKansalaisuus(Kansalaisuus kansalaisuus) {
        String serialized = Ryhmatunnus.KANSALAISUUS.getCode()
                + kansalaisuus.getMuutostapa().getNumber()
                + (kansalaisuus.getCode().length() > 3 ? "998" : kansalaisuus.getCode())
                + (kansalaisuus.getValid() ? "1" : "2")
                + serializeDate(kansalaisuus.getStartDate())
                + serializeDate(kansalaisuus.getEndDate());
        if (kansalaisuus.getCode().length() > 3) {
            serialized = String.join("|", serialized, serializeAdditionalInformation(kansalaisuus));
        }
        return serialized;
    }

    private static String serializeAdditionalInformation(Kansalaisuus kansalaisuus) {
        return Ryhmatunnus.LISATIETO.getCode()
                + kansalaisuus.getMuutostapa().getNumber()
                + serializeString(kansalaisuus.getCode(), 30);
    }

}
