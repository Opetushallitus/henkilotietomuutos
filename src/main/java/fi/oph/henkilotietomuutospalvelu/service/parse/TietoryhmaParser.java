package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

public interface TietoryhmaParser<T extends Tietoryhma> {

    T parse(String tietoryhma, String... tarkentavatTietoryhmat);

    String serialize(T tietoryhma);

}
