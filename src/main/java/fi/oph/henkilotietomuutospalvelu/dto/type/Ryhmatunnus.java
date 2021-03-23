package fi.oph.henkilotietomuutospalvelu.dto.type;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.service.parse.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Ryhmatunnus {

    HENKILOTUNNUS_KORJAUS("001", new HenkilotunnuskorjausParser()),
    AIDINKIELI("002", new AidinkieliParser()),
    SUKUPUOLI("003", new SukupuoliParser()),
    HENKILO_NIMI("004", new HenkiloNameParser()),
    HENKILO_NIMENMUUTOS("005", new HenkiloNameChangeParser()),
    KANSALAISUUS("007", new KansalaisuusParser()),
    SYNTYMAKOTIKUNTA("008", new SyntymaKotikuntaParser()),
    ULKOMAINEN_SYNTYMAPAIKKA("009", new UlkomainenSyntymapaikkaParser()),
    KUOLINPAIVA("013", new KuolinpaivaParser()),
    TURVAKIELTO("015", new TurvakieltoParser()),
    KOTIMAINEN_OSOITE("101", KotimainenOsoiteParser.INSTANCE),
    KOTIMAINEN_OSOITE_TILAPAINEN("102", new TilapainenKotimainenOsoiteParser()),
    POSTIOSOITE("103", new PostiosoiteParser()),
    ULKOMAINEN_OSOITE("104", UlkomainenOsoiteParser.INSTANCE),
    ULKOMAINEN_OSOITE_TILAPAINEN("105", new TilapainenUlkomainenOsoiteParser()),
    KOTIKUNTA("204", new KotikuntaParser()),
    HUOLTAJA("305", new HuoltajaParser()),
    EDUNVALVONTA("306", new EdunvalvontaParser()),
    EDUNVALVOJA("307", new EdunvalvojaParser()),
    EDUNVALVONTAVALTUUTUS("316", new EdunvalvontaValtuutusParser()),
    EDUNVALVONTAVALTUUTETTU("317", new EdunvalvontaValtuutettuParser()),
    AMMATTI("401", new AmmattiParser()),
    SAHKOPOSTIOSOITE("421", new SahkopostiOsoiteParser()),
    ULKOMAINEN_HENKILONUMERO("422", new UlkomainenHenkilonumeroParser()),
    KUTSUMANIMI("423", new KutsumanimiParser()),
    // tarkentavat tietoryhmät käsitellään osana toista tietoryhmää, siksi parser on null
    OIKEUS("320", true, null),
    HENKILOTUNNUKSETON_HENKILO("451", true, null),
    LISATIETO("452", true, null);

    private final String code;
    private final boolean tarkentava;
    private final TietoryhmaParser<? extends Tietoryhma<?>> parser;

    private static final Map<String, Ryhmatunnus> map =
            Arrays.stream(Ryhmatunnus.values()).collect(Collectors.toMap(type -> type.code, type -> type));

    Ryhmatunnus(String code, TietoryhmaParser<? extends Tietoryhma<?>> parser) {
        this(code, false, parser);
    }

    Ryhmatunnus(String code, boolean tarkentava, TietoryhmaParser<? extends Tietoryhma<?>> parser) {
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

    public TietoryhmaParser<? extends Tietoryhma<?>> getParser() {
        return parser;
    }

    public static Ryhmatunnus getEnum(String code) {
        return map.get(code);
    }

}
