package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

public interface TietoryhmaParser {

    Tietoryhma parse(String tietoryhma, String... tarkentavatTietoryhmat);

    String serialize(Tietoryhma tietoryhma);
}
