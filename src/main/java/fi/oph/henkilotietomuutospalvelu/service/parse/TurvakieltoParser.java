package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Turvakielto;

import java.time.LocalDate;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;

public class TurvakieltoParser {

    private static final String INDEFINITE_DATESTRING = "99990000";

    static Turvakielto parseTurvakielto(String value) {
        LocalDate endDate = null;
        String dateStr = parseString(value, 4, 8);
        /* Toistaiseksi voimassa oleva turvakielto merkitään loppuajalla 99990000. */
        if (!dateStr.equals(INDEFINITE_DATESTRING)) {
            endDate = parseDate(value, 4);
        }

        return Turvakielto.builder()
                .ryhmatunnus(Ryhmatunnus.TURVAKIELTO)
                .muutostapa(parseMuutosTapa(value))
                .endDate(endDate)
                .build();
    }

    static String serializeTurvakielto(Turvakielto turvakielto) {
        return Ryhmatunnus.TURVAKIELTO.getCode()
                + turvakielto.getMuutostapa().getNumber()
                + (turvakielto.getEndDate() == null ? INDEFINITE_DATESTRING : serializeDate(turvakielto.getEndDate()));
    }

}
