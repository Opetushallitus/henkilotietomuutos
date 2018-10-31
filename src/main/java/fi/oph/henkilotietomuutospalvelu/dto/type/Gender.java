package fi.oph.henkilotietomuutospalvelu.dto.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Gender {
    // Don't change order. db uses enum types.
    MALE("1"),
    FEMALE("2"),
    MISSING(" ");

    private final String code;

    private static final Map<String, Gender> map =
            Arrays.stream(Gender.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    Gender(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Gender getEnum(String code) {
        return map.get(code);
    }
}
