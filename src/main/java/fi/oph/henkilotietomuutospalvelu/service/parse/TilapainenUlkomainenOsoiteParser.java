package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.TilapainenUlkomainenOsoite;

public class TilapainenUlkomainenOsoiteParser implements TietoryhmaParser<TilapainenUlkomainenOsoite> {

    public static final TilapainenUlkomainenOsoiteParser INSTANCE = new TilapainenUlkomainenOsoiteParser();

    @Override
    public TilapainenUlkomainenOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return TilapainenUlkomainenOsoite.from(
                UlkomainenOsoiteParser.INSTANCE.parse(tietoryhma, tarkentavatTietoryhmat)
        );
    }

    @Override
    public String serialize(TilapainenUlkomainenOsoite tietoryhma) {
        return UlkomainenOsoiteParser.INSTANCE.serialize(tietoryhma);
    }
}
