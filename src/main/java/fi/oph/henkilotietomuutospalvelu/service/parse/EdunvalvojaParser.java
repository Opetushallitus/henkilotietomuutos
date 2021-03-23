package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvoja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkilotunnuksetonHenkilo;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class EdunvalvojaParser implements TietoryhmaParser<Edunvalvoja> {

    public static final EdunvalvojaParser INSTANCE = new EdunvalvojaParser();
    private static final HenkilotunnuksetonHenkiloParser HENKILO_PARSER = HenkilotunnuksetonHenkiloParser.INSTANCE;

    public Edunvalvoja parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Edunvalvoja.builder()
                .ryhmatunnus(Ryhmatunnus.EDUNVALVOJA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .hetu(parseString(tietoryhma, 4, 11))
                .yTunnus(parseString(tietoryhma, 15, 9))
                .municipalityCode(parseString(tietoryhma, 24, 3))
                .oikeusaputoimistoKoodi(parseString(tietoryhma, 27, 6))
                .startDate(parseDate(tietoryhma, 33))
                .endDate(parseDate(tietoryhma, 41))
                .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                .build();
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

    public String serialize(Edunvalvoja edunvalvoja) {
        String serialized = Ryhmatunnus.EDUNVALVOJA.getCode()
                + edunvalvoja.getMuutostapa().getNumber()
                + serializeString(edunvalvoja.getHetu(), 11)
                + serializeString(edunvalvoja.getYTunnus(), 9)
                + serializeString(edunvalvoja.getMunicipalityCode(), 3)
                + serializeString(edunvalvoja.getOikeusaputoimistoKoodi(), 6)
                + serializeDate(edunvalvoja.getStartDate())
                + serializeDate(edunvalvoja.getEndDate());
        if (edunvalvoja.getHenkilotunnuksetonHenkilo() != null) {
            serialized = String.join("|", serialized,
                    HENKILO_PARSER.serialize(edunvalvoja.getHenkilotunnuksetonHenkilo()));
        }
        return serialized;
    }

}
