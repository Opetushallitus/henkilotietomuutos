package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.TilapainenUlkomainenOsoite;

public class TilapainenUlkomainenOsoiteParser implements TietoryhmaParser {

    private static final TietoryhmaParser ULKOMAINEN_OSOITE = new UlkomainenOsoiteParser();

    @Override
    public TilapainenUlkomainenOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return TilapainenUlkomainenOsoite.from(
                ULKOMAINEN_OSOITE.parse(tietoryhma, tarkentavatTietoryhmat)
        );
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        return ULKOMAINEN_OSOITE.serialize(tietoryhma);
    }
}
