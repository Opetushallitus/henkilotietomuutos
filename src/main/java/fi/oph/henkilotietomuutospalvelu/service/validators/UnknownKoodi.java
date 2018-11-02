package fi.oph.henkilotietomuutospalvelu.service.validators;

import lombok.Getter;

public enum UnknownKoodi {
    KIELIKOODI_TUNTEMATON("99"),
    KUNTAKOODI_TUNTEMATON("999"),
    KANSALAISUUSKOODI_TUNTEMATON("998"),
    HUOLTAJUUSTYYPPI_TUNTEMATON("06"), // Muu huoltaja
    ;

    @Getter
    private String koodi;

    UnknownKoodi(String koodi) {
        this.koodi = koodi;
    }
}
