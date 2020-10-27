package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;

public class TietoryhmaSerializerUtil {

    public static String serializeTietoryhma(Tietoryhma tietoryhma) {
        switch (tietoryhma.getRyhmatunnus()) {
            case HENKILOTUNNUS_KORJAUS:
                return HenkilotunnuskorjausParser.serializeHenkilotunnuskorjaus((Henkilotunnuskorjaus) tietoryhma);
            case AIDINKIELI:
                return AidinkieliParser.serializeAidinkieli((Aidinkieli) tietoryhma);
            case SUKUPUOLI:
                return SukupuoliParser.serializeSukupuoli((Sukupuoli) tietoryhma);
            case HENKILO_NIMI:
                return HenkiloNameParser.serializeHenkiloName((HenkiloName) tietoryhma);
            case HENKILO_NIMENMUUTOS:
                return HenkiloNameChangeParser.serializeHenkiloNameChange((HenkiloNameChange) tietoryhma);
            case KANSALAISUUS:
                return KansalaisuusParser.serializeKansalaisuus((Kansalaisuus) tietoryhma);
            case SYNTYMAKOTIKUNTA:
                return SyntymaKotikuntaParser.serializeSyntymaKotikunta((SyntymaKotikunta) tietoryhma);
            case ULKOMAINEN_SYNTYMAPAIKKA:
                return UlkomainenSyntymapaikkaParser.serializeUlkomainenSyntymapaikka((UlkomainenSyntymapaikka) tietoryhma);
            case KUOLINPAIVA:
                return KuolinpaivaParser.serializeKuolinpaiva((Kuolinpaiva) tietoryhma);
            case TURVAKIELTO:
                return TurvakieltoParser.serializeTurvakielto((Turvakielto) tietoryhma);
            case KOTIMAINEN_OSOITE:
                return KotimainenOsoiteParser.serializeKotimainenOsoite((KotimainenOsoite) tietoryhma);
            case KOTIMAINEN_OSOITE_TILAPAINEN:
                return KotimainenOsoiteParser.serializeKotimainenOsoite((TilapainenKotimainenOsoite) tietoryhma);
            case POSTIOSOITE:
                return PostiosoiteParser.serializePostiosoite((Postiosoite) tietoryhma);
            case ULKOMAINEN_OSOITE:
                return UlkomainenOsoiteParser.serializeUlkomainenOsoite((UlkomainenOsoite) tietoryhma);
            case ULKOMAINEN_OSOITE_TILAPAINEN:
                return UlkomainenOsoiteParser.serializeUlkomainenOsoite((TilapainenUlkomainenOsoite) tietoryhma);
            case KOTIKUNTA:
                return KotikuntaParser.serializeKotikunta((Kotikunta) tietoryhma);
            case HUOLTAJA:
                return HuoltajaParser.serializeHuoltaja((Huoltaja) tietoryhma);
            case EDUNVALVONTA:
                return EdunvalvontaParser.serializeEdunvalvonta((Edunvalvonta) tietoryhma);
            case EDUNVALVOJA:
                return EdunvalvojaParser.serializeEdunvalvoja((Edunvalvoja) tietoryhma);
            case EDUNVALVONTAVALTUUTUS:
                return EdunvalvontaValtuutusParser.serializeEdunvalvontaValtuutus((EdunvalvontaValtuutus) tietoryhma);
            case EDUNVALVONTAVALTUUTETTU:
                return EdunvalvontaValtuutettuParser.serializeEdunvalvontaValtuutettu((EdunvalvontaValtuutettu) tietoryhma);
            case AMMATTI:
                return AmmattiParser.serializeAmmatti((Ammatti) tietoryhma);
            case SAHKOPOSTIOSOITE:
                return SahkopostiOsoiteParser.serializeSahkopostiOsoite((SahkopostiOsoite) tietoryhma);
            case ULKOMAINEN_HENKILONUMERO:
                return UlkomainenHenkilonumeroParser.serializeUlkomainenHenkilonumero((UlkomainenHenkilonumero) tietoryhma);
            case KUTSUMANIMI:
                return KutsumanimiParser.serializeKutsumanimi((Kutsumanimi) tietoryhma);
            default: return null;
        }
    }

}
