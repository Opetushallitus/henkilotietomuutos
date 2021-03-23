package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Oikeus;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class OikeusParser implements TietoryhmaParser<Oikeus> {

    public static final OikeusParser INSTANCE = new OikeusParser();

    public Oikeus parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Oikeus.builder()
                .ryhmatunnus(Ryhmatunnus.OIKEUS)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .koodi(parseString(tietoryhma, 4, 4))
                .alkupvm(parseDate(tietoryhma, 8))
                .loppupvm(parseDate(tietoryhma, 16))
                .build();
    }

    public String serialize(Oikeus oikeus) {
        return Ryhmatunnus.OIKEUS.getCode()
                + oikeus.getMuutostapa().getNumber()
                + serializeString(oikeus.getKoodi(), 4)
                + serializeDate(oikeus.getAlkupvm())
                + serializeDate(oikeus.getLoppupvm());
    }
}
