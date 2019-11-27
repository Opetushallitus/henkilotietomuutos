package fi.oph.henkilotietomuutospalvelu.dto.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Ryhmatunnus {

    HENKILOTUNNUS_KORJAUS("001"),
    AIDINKIELI("002"),
    SUKUPUOLI("003"),
    HENKILO_NIMI("004"),
    HENKILO_NIMENMUUTOS("005"),
    KANSALAISUUS("007"),
    SYNTYMAKOTIKUNTA("008"),
    ULKOMAINEN_SYNTYMAPAIKKA("009"),
    KUOLINPAIVA("013"),
    TURVAKIELTO("015"),
    KOTIMAINEN_OSOITE("101"),
    KOTIMAINEN_OSOITE_TILAPAINEN("102"),
    POSTIOSOITE("103"),
    ULKOMAINEN_OSOITE("104"),
    ULKOMAINEN_OSOITE_TILAPAINEN("105"),
    KOTIKUNTA("204"),
    HUOLTAJA("305"),
    EDUNVALVONTA("306"),
    EDUNVALVOJA("307"),
    EDUNVALVONTAVALTUUTUS("316"),
    EDUNVALVONTAVALTUUTETTU("317"),
    AMMATTI("401"),
    SAHKOPOSTIOSOITE("421"),
    ULKOMAINEN_HENKILONUMERO("422"),
    KUTSUMANIMI("423"),
    HENKILOTUNNUKSETON_HENKILO("451", true),
    LISATIETO("452", true);

    private final String code;
    private final boolean tarkentava;

    private static final Map<String, Ryhmatunnus> map =
            Arrays.stream(Ryhmatunnus.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    Ryhmatunnus(String code) {
        this(code, false);
    }

    Ryhmatunnus(String code, boolean tarkentava) {
        this.code = code;
        this.tarkentava = tarkentava;
    }

    public String getCode() {
        return code;
    }

    public boolean isTarkentava() {
        return tarkentava;
    }

    public static Ryhmatunnus getEnum(String code) {
        return map.get(code);
    }

}
