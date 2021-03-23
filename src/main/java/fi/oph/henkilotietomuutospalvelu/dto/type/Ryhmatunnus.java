package fi.oph.henkilotietomuutospalvelu.dto.type;

import fi.oph.henkilotietomuutospalvelu.service.parse.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Ryhmatunnus {

    HENKILOTUNNUS_KORJAUS("001", HenkilotunnuskorjausParser.INSTANCE),
    AIDINKIELI("002", AidinkieliParser.INSTANCE),
    SUKUPUOLI("003", SukupuoliParser.INSTANCE),
    HENKILO_NIMI("004", HenkiloNameParser.INSTANCE),
    HENKILO_NIMENMUUTOS("005", HenkiloNameChangeParser.INSTANCE),
    KANSALAISUUS("007", KansalaisuusParser.INSTANCE),
    SYNTYMAKOTIKUNTA("008", SyntymaKotikuntaParser.INSTANCE),
    ULKOMAINEN_SYNTYMAPAIKKA("009", UlkomainenSyntymapaikkaParser.INSTANCE),
    KUOLINPAIVA("013", KuolinpaivaParser.INSTANCE),
    TURVAKIELTO("015", TurvakieltoParser.INSTANCE),
    KOTIMAINEN_OSOITE("101", KotimainenOsoiteParser.INSTANCE),
    KOTIMAINEN_OSOITE_TILAPAINEN("102", TilapainenKotimainenOsoiteParser.INSTANCE),
    POSTIOSOITE("103", PostiosoiteParser.INSTANCE),
    ULKOMAINEN_OSOITE("104", UlkomainenOsoiteParser.INSTANCE),
    ULKOMAINEN_OSOITE_TILAPAINEN("105", TilapainenUlkomainenOsoiteParser.INSTANCE),
    KOTIKUNTA("204", KotikuntaParser.INSTANCE),
    HUOLTAJA("305", HuoltajaParser.INSTANCE),
    EDUNVALVONTA("306", EdunvalvontaParser.INSTANCE),
    EDUNVALVOJA("307", EdunvalvojaParser.INSTANCE),
    EDUNVALVONTAVALTUUTUS("316", EdunvalvontaValtuutusParser.INSTANCE),
    EDUNVALVONTAVALTUUTETTU("317", EdunvalvontaValtuutettuParser.INSTANCE),
    OIKEUS("320", true, OikeusParser.INSTANCE),
    AMMATTI("401", AmmattiParser.INSTANCE),
    SAHKOPOSTIOSOITE("421", SahkopostiOsoiteParser.INSTANCE),
    ULKOMAINEN_HENKILONUMERO("422", UlkomainenHenkilonumeroParser.INSTANCE),
    KUTSUMANIMI("423", KutsumanimiParser.INSTANCE),
    // henkilötunnukseton henkilö ja lisätieto käsitellään osana toista tietoryhmää
    HENKILOTUNNUKSETON_HENKILO("451", true, null),
    LISATIETO("452", true, null);

    private final String code;
    private final boolean tarkentava;
    private final TietoryhmaParser<?> parser;

    private static final Map<String, Ryhmatunnus> map =
            Arrays.stream(Ryhmatunnus.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    Ryhmatunnus(String code, TietoryhmaParser<?> parser) {
        this(code, false, parser);
    }

    Ryhmatunnus(String code, boolean tarkentava, TietoryhmaParser<?> parser) {
        this.code = code;
        this.tarkentava = tarkentava;
        this.parser = parser;
    }

    public String getCode() {
        return code;
    }

    public boolean isTarkentava() {
        return tarkentava;
    }

    public TietoryhmaParser<?> getParser() {
        return parser;
    }

    public static Ryhmatunnus getEnum(String code) {
        return map.get(code);
    }

}
