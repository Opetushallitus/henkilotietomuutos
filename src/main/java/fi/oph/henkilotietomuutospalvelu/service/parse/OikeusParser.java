package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Oikeus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class OikeusParser implements TietoryhmaParser {

    @Override
    public Oikeus parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return Oikeus.builder()
                .ryhmatunnus(Ryhmatunnus.OIKEUS)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .koodi(parseString(tietoryhma, 4, 4))
                .alkupvm(parseDate(tietoryhma, 8))
                .loppupvm(parseDate(tietoryhma, 16))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        Oikeus oikeus = (Oikeus) tietoryhma;
        return Ryhmatunnus.OIKEUS.getCode()
                + oikeus.getMuutostapa().getNumber()
                + serializeString(oikeus.getKoodi(), 4)
                + serializeDate(oikeus.getAlkupvm())
                + serializeDate(oikeus.getLoppupvm());
    }
}
