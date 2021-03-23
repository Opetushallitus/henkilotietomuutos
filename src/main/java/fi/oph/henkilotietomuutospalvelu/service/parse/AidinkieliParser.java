package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Aidinkieli;
import lombok.extern.slf4j.Slf4j;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeAdditionalInformation;

@Slf4j
public class AidinkieliParser implements TietoryhmaParser<Aidinkieli> {

    public static final AidinkieliParser INSTANCE = new AidinkieliParser();

    public Aidinkieli parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        String languageCode = parseString(tietoryhma, 4,2);

        Aidinkieli aidinkieli = Aidinkieli.builder()
                .ryhmatunnus(Ryhmatunnus.AIDINKIELI)
                .muutostapa(parseMuutosTapa(tietoryhma))
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

    public String serialize(Aidinkieli aidinkieli) {
        String serialized = Ryhmatunnus.AIDINKIELI.getCode()
                + aidinkieli.getMuutostapa().getNumber()
                + aidinkieli.getLanguageCode();
        if (aidinkieli.getAdditionalInformation() != null) {
            serialized = String.join(
                    "|", serialized, serializeAdditionalInformation(aidinkieli.getAdditionalInformation()));
        }
        return serialized;
    }

}
