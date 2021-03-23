package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkiloName;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HenkiloNameParser implements TietoryhmaParser<HenkiloName> {

    public static final HenkiloNameParser INSTANCE = new HenkiloNameParser();
    private static final int NAME_LENGTH = 100;

    public HenkiloName parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return HenkiloName.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILO_NIMI)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .lastName(parseString(tietoryhma, 4, NAME_LENGTH))
                .firstNames(parseString(tietoryhma, 104, NAME_LENGTH))
                .lastUpdateDate(parseDate(tietoryhma, 204))
                .additionalInformation(parseCharacter(tietoryhma, 212))
                .build();
    }

    public String serialize(HenkiloName henkiloName) {
        return Ryhmatunnus.HENKILO_NIMI.getCode()
                + henkiloName.getMuutostapa().getNumber()
                + serializeString(henkiloName.getLastName(), NAME_LENGTH)
                + serializeString(henkiloName.getFirstNames(), NAME_LENGTH)
                + serializeDate(henkiloName.getLastUpdateDate())
                + henkiloName.getAdditionalInformation();
    }
}
