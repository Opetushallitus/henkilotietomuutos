package fi.oph.henkilotietomuutospalvelu.dto.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Huollonjako {
    // Do not change order. Enum number is used in db.
    SUORITETTU("1"),
    SUORITTAMATTA(" "),
    SISALLOSTA_SOVITTU("2");

    private final String code;

    private static final Map<String, Huollonjako> map =
            Arrays.stream(Huollonjako.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    Huollonjako(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Huollonjako getEnum(String code) {
        return map.get(code);
    }

}
