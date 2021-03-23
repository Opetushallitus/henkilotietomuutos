package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.TilapainenKotimainenOsoite;

public class TilapainenKotimainenOsoiteParser implements TietoryhmaParser<TilapainenKotimainenOsoite> {

    @Override
    public TilapainenKotimainenOsoite parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        return TilapainenKotimainenOsoite.from(
                KotimainenOsoiteParser.INSTANCE.parse(tietoryhma, tarkentavatTietoryhmat)
        );
    }

    @Override
    public String serialize(TilapainenKotimainenOsoite tietoryhma) {
        return KotimainenOsoiteParser.INSTANCE.serialize(tietoryhma);
    }

}
