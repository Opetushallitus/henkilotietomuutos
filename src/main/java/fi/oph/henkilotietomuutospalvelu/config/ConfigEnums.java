package fi.oph.henkilotietomuutospalvelu.config;

public enum ConfigEnums {
    SUBSYSTEMCODE("henkilotietomuutos-service"),
    SERVICENAME("henkilotietomuutos");

    private final String value;

    ConfigEnums(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConfigEnums fromValue(String v) {
        for (ConfigEnums c: ConfigEnums.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
