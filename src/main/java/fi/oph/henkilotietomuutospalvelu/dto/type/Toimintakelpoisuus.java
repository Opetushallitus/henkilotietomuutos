package fi.oph.henkilotietomuutospalvelu.dto.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Toimintakelpoisuus {

    RAJOITTAMATON("1"),
    RAJOITETTU_OSITTAIN("2"),
    HENKILO_VAJAAVALTAINEN("3");

    private final String code;

    private static final Map<String, Toimintakelpoisuus> map =
            Arrays.stream(Toimintakelpoisuus.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    Toimintakelpoisuus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Toimintakelpoisuus getEnum(String code) {
        return map.get(code);
    }

}
