package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.EdunvalvontaValtuutettu;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkilotunnuksetonHenkilo;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvontaValtuutettuParser implements TietoryhmaParser<EdunvalvontaValtuutettu> {

    public static final EdunvalvontaValtuutettuParser INSTANCE = new EdunvalvontaValtuutettuParser();
    private static final HenkilotunnuksetonHenkiloParser HENKILO_PARSER = HenkilotunnuksetonHenkiloParser.INSTANCE;

    public EdunvalvontaValtuutettu parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return EdunvalvontaValtuutettu.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVONTAVALTUUTETTU)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .hetu(parseString(tietoryhma, 4, 11))
                .startDate(parseDate(tietoryhma, 15))
                .endDate(parseDate(tietoryhma, 23))
                .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                .build();
    }

    public String serialize(EdunvalvontaValtuutettu valtuutettu) {
        String serialized = Ryhmatunnus.EDUNVALVONTAVALTUUTETTU.getCode()
                + valtuutettu.getMuutostapa().getNumber()
                + serializeString(valtuutettu.getHetu(), 11)
                + serializeDate(valtuutettu.getStartDate())
                + serializeDate(valtuutettu.getEndDate());
        if (valtuutettu.getHenkilotunnuksetonHenkilo() != null) {
            serialized = String.join("|", serialized,
                    HENKILO_PARSER.serialize(valtuutettu.getHenkilotunnuksetonHenkilo()));
        }
        return serialized;
    }

    private HenkilotunnuksetonHenkilo parseHenkilotunnuksetonHenkilo(String... tarkentavatTietoryhmat) {
        if (tarkentavatTietoryhmat.length < 1) {
            return null;
        }
        return HENKILO_PARSER.parse(
                tarkentavatTietoryhmat[0],
                tarkentavatTietoryhmat.length > 1 ? new String[] { tarkentavatTietoryhmat[1] } : new String[0]
        );
    }

}
