package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkiloName;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HenkiloNameParser {

    private static final int NAME_LENGTH = 100;

    static HenkiloName parseHenkiloName(String value) {
        return HenkiloName.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILO_NIMI)
                .muutostapa(parseMuutosTapa(value))
                .lastName(parseString(value, 4, NAME_LENGTH))
                .firstNames(parseString(value, 104, NAME_LENGTH))
                .lastUpdateDate(parseDate(value, 204))
                .additionalInformation(parseCharacter(value, 212))
                .build();
    }

    static String serializeHenkiloName(HenkiloName henkiloName) {
        return Ryhmatunnus.HENKILO_NIMI.getCode()
                + henkiloName.getMuutostapa().getNumber()
                + serializeString(henkiloName.getLastName(), NAME_LENGTH)
                + serializeString(henkiloName.getFirstNames(), NAME_LENGTH)
                + serializeDate(henkiloName.getLastUpdateDate())
                + henkiloName.getAdditionalInformation();
    }
}
