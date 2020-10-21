package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Aidinkieli;
import lombok.extern.slf4j.Slf4j;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;

@Slf4j
public class AidinkieliParser {

    private static final String ADDITIONAL_INFORMATION_FORMAT = "%1$-30s";

    static Aidinkieli parseAidinkieli(String value, String... tarkentavatTietoryhmat) {
        String languageCode = parseString(value, 4,2);

        Aidinkieli aidinkieli = Aidinkieli.builder()
                .ryhmatunnus(Ryhmatunnus.AIDINKIELI)
                .muutostapa(parseMuutosTapa(value))
                .languageCode(languageCode).build();

        if (languageCode.equals("98")) {
            if (tarkentavatTietoryhmat.length > 0) {
                aidinkieli.setAdditionalInformation(parseAdditionalInformation(tarkentavatTietoryhmat[0]));
            } else {
                log.warn("Missing additional language information.");
            }
        }

        return aidinkieli;
    }

    static String serializeAidinkieli(Aidinkieli aidinkieli) {
        String serialized = Ryhmatunnus.AIDINKIELI.getCode()
                + aidinkieli.getMuutostapa().getNumber()
                + aidinkieli.getLanguageCode();
        if (aidinkieli.getAdditionalInformation() != null) {
            serialized = String.join(
                    "|", serialized, serializeAdditionalInformation(aidinkieli.getAdditionalInformation()));
        }
        return serialized;
    }

    private static String serializeAdditionalInformation(String additionalInformation) {
        return Ryhmatunnus.LISATIETO.getCode()
                + Muutostapa.LISATIETO.getNumber()
                + String.format(ADDITIONAL_INFORMATION_FORMAT, additionalInformation);
    }

}
