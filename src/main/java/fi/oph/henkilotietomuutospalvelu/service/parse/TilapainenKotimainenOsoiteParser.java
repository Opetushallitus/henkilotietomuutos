package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.TilapainenKotimainenOsoite;

public class TilapainenKotimainenOsoiteParser implements TietoryhmaParser {

    private static final KotimainenOsoiteParser KOTIMAINEN_OSOITE_PARSER = new KotimainenOsoiteParser();

    @Override
    public TilapainenKotimainenOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return TilapainenKotimainenOsoite.from(
                KOTIMAINEN_OSOITE_PARSER.parse(tietoryhma, tarkentavatTietoryhmat)
        );
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        return KOTIMAINEN_OSOITE_PARSER.serialize(tietoryhma);
    }
}
