package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkiloName;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HenkiloNameParser implements TietoryhmaParser {

    private static final int NAME_LENGTH = 100;

    @Override
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

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        HenkiloName henkiloName = (HenkiloName) tietoryhma;
        return Ryhmatunnus.HENKILO_NIMI.getCode()
                + henkiloName.getMuutostapa().getNumber()
                + serializeString(henkiloName.getLastName(), NAME_LENGTH)
                + serializeString(henkiloName.getFirstNames(), NAME_LENGTH)
                + serializeDate(henkiloName.getLastUpdateDate())
                + henkiloName.getAdditionalInformation();
    }
}
