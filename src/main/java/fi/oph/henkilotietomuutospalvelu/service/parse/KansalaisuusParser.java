package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kansalaisuus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;

public class KansalaisuusParser implements TietoryhmaParser {

    @Override
    public Kansalaisuus parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        String code = parseString(tietoryhma, 4, 3);
        if (code.equals("998")) {
            code = parseAdditionalInformation(tarkentavatTietoryhmat[0]);
        }

        return Kansalaisuus.builder()
                .ryhmatunnus(Ryhmatunnus.KANSALAISUUS)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .code(code)
                .valid(parseCharacter(tietoryhma, 7).equals("1"))
                .startDate(parseDate(tietoryhma, 8))
                .endDate(parseDate(tietoryhma,16))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Kansalaisuus kansalaisuus = (Kansalaisuus) tietoryhma;
        String serialized = Ryhmatunnus.KANSALAISUUS.getCode()
                + kansalaisuus.getMuutostapa().getNumber()
                + (kansalaisuus.getCode().length() > 3 ? "998" : kansalaisuus.getCode())
                + (kansalaisuus.getValid() ? "1" : "2")
                + serializeDate(kansalaisuus.getStartDate())
                + serializeDate(kansalaisuus.getEndDate());
        if (kansalaisuus.getCode().length() > 3) {
            serialized = String.join("|", serialized, serializeAdditionalInformation(kansalaisuus.getCode()));
        }
        return serialized;
    }

}
