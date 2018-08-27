package fi.oph.henkilotietomuutospalvelu.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KoodistoYhteystietoAlkupera {
    VTJ("alkupera1"),
    ;

    private final String koodi;
}
