package fi.oph.henkilotietomuutospalvelu.dto.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MuutosType {
    UUSI("1"),
    VANHA("0"),
    JATKETTU("J"),
    TYHJA("-1");

    private final String code;

    private static final Map<String, MuutosType> map =
            Arrays.stream(MuutosType.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    MuutosType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MuutosType getEnum(String code) {
        return map.get(code);
    }

}
