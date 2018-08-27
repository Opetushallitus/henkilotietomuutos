package fi.oph.henkilotietomuutospalvelu.service.build;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;

import java.util.List;

public class HenkiloJsonAppender {

    static HenkiloJsonBuilder appendHetuChange(Henkilotunnuskorjaus hetu) {
        return new HenkiloJsonBuilder()
                .appendKeyValue("hetu", hetu.getHetu());
    }

    static HenkiloJsonBuilder appendNameChange(HenkiloNameChange nameChange) {
        HenkiloJsonBuilder builder = new HenkiloJsonBuilder();
        if (NameType.ETUNIMI.equals(nameChange.getNameType())) {
            builder.appendKeyValue("etunimet", nameChange.getName());
        } else if (NameType.SUKUNIMI.equals(nameChange.getNameType())) {
            builder.appendKeyValue("sukunimi", nameChange.getName());
        } else if (NameType.KUTSUMANIMI.equals(nameChange.getNameType())) {
            builder.appendKeyValue("kutsumanimi", nameChange.getName());
        }
        return builder;
    }

    static HenkiloJsonBuilder appendKuolinpaiva(Kuolinpaiva kuolinpaiva) {
        return new HenkiloJsonBuilder()
                .appendKeyValue("kuolinpaiva", kuolinpaiva.getDateOfDeath().toString());
    }

    static HenkiloJsonBuilder appendAidinkieli(Aidinkieli aidinkieli) {
        return new HenkiloJsonBuilder()
                .appendKey("aidinkieli")
                .appendOpenBrace()
                .appendKeyValue("kieliKoodi", aidinkieli.getLanguageCode())
                .appendComma()
                .appendKeyValue("kieliTyyppi", aidinkieli.getAdditionalInformation())
                .appendCloseBrace();
    }

    static HenkiloJsonBuilder appendKansalaisuudet(List<Kansalaisuus> kansalaisuudet) {
        HenkiloJsonBuilder builder = new HenkiloJsonBuilder()
                .appendKey("kansalaisuus")
                .appendOpenBracket();

        for (int i = 0; i < kansalaisuudet.size(); i++) {
            builder.appendOpenBrace();
            builder.appendKeyValue("kansalaisuusKoodi", kansalaisuudet.get(i).getCode());
            builder.appendCloseBrace();
            if (i != kansalaisuudet.size() - 1) {
                builder.appendComma();
            }
        }

        return builder.appendCloseBracket();
    }

}
