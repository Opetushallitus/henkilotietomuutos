package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkilotunnuksetonHenkilo;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.serializeAdditionalInformation;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HenkilotunnuksetonHenkiloParser implements TietoryhmaParser {

    @Override
    public HenkilotunnuksetonHenkilo parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        HenkilotunnuksetonHenkilo henkilo = HenkilotunnuksetonHenkilo.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILOTUNNUKSETON_HENKILO)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .dateOfBirth(parseDate(tietoryhma, 4))
                .gender(Gender.getEnum(parseCharacter(tietoryhma, 12)))
                .lastname(parseString(tietoryhma, 13, 100))
                .firstNames(parseString(tietoryhma, 113, 100))
                .nationality(parseString(tietoryhma, 213, 3))
                .build();
        if ("998".equals(henkilo.getNationality())) {
            henkilo.setAdditionalInformation(parseAdditionalInformation(tarkentavatTietoryhmat[0]));
        }
        return henkilo;
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        HenkilotunnuksetonHenkilo henkilo = (HenkilotunnuksetonHenkilo) tietoryhma;
        String serialized = Ryhmatunnus.HENKILOTUNNUKSETON_HENKILO.getCode()
                + henkilo.getMuutostapa().getNumber()
                + serializeDate(henkilo.getDateOfBirth())
                + henkilo.getGender().getCode()
                + serializeString(henkilo.getLastname(), 100)
                + serializeString(henkilo.getFirstNames(), 100)
                + serializeString(henkilo.getNationality(), 3);
        if ("998".equals(henkilo.getNationality())) {
            serialized = String.join("|", serialized,
                    serializeAdditionalInformation(henkilo.getAdditionalInformation()));
        }
        return serialized;
    }

}
