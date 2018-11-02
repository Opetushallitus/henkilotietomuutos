package fi.oph.henkilotietomuutospalvelu.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Koodisto {

    POSTI("posti"),
    MAAT_JA_VALTIOT_2("maatjavaltiot2"),
    KUNTA("kunta"),
    KIELI("kieli"),
    HUOLTAJUUSTYYPPI("huoltajuustyyppi")
    ;

    private final String uri;

}
