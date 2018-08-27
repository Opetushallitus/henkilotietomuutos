package fi.oph.henkilotietomuutospalvelu.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KoodistoYhteystietoTyyppi {
    KOTIMAINEN("yhteystietotyyppi4"),
    ULKOMAINEN("yhteystietotyyppi5"),
    SAHKOINEN_OSOITE("yhteystietotyyppi8"),
    TILAPAINEN_KOTIMAINEN("yhteystietotyyppi9"),
    TILAPAINEN_ULKOMAINEN("yhteystietotyyppi10"),
    KOTIMAINEN_POSTIOSOITE("yhteystietotyyppi11"),
    ULKOMAINEN_POSTIOSOITE("yhteystietotyyppi12"),
    ;

    private final String koodi;
}
