package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Turvakielto;

import java.time.LocalDate;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;

public class TurvakieltoParser implements TietoryhmaParser {

    private static final String INDEFINITE_DATESTRING = "99990000";

    @Override
    public Turvakielto parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        LocalDate endDate = null;
        String dateStr = parseString(tietoryhma, 4, 8);
        /* Toistaiseksi voimassa oleva turvakielto merkitään loppuajalla 99990000. */
        if (!dateStr.equals(INDEFINITE_DATESTRING)) {
            endDate = parseDate(tietoryhma, 4);
        }

        return Turvakielto.builder()
                .ryhmatunnus(Ryhmatunnus.TURVAKIELTO)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .endDate(endDate)
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Turvakielto turvakielto = (Turvakielto) tietoryhma;
        return Ryhmatunnus.TURVAKIELTO.getCode()
                + turvakielto.getMuutostapa().getNumber()
                + (turvakielto.getEndDate() == null ? INDEFINITE_DATESTRING : serializeDate(turvakielto.getEndDate()));
    }

}
