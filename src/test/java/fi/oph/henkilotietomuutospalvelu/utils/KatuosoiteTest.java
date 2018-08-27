package fi.oph.henkilotietomuutospalvelu.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KatuosoiteTest {

    @Test
    public void getAsStringToimii() {
        String katuosoite = Katuosoite.builder()
                .lahiosoiteFi("Testaajankatu")
                .katunumero("14")
                .porraskirjain("A")
                .huonenumero("12")
                .jakokirjain("b")
                .build()
                .getAsString("fi");

        assertThat(katuosoite).isEqualTo("Testaajankatu 14 A 12 b");
    }

    @Test
    public void getAsStringPoistaaTarpeettomanHuonenumeron() {
        String katuosoite = Katuosoite.builder()
                .lahiosoiteFi("Testaajankatu")
                .katunumero("14")
                .porraskirjain(" ")
                .huonenumero("000")
                .build()
                .getAsString("fi");

        assertThat(katuosoite).isEqualTo("Testaajankatu 14");
    }

    @Test
    public void getAsStringPoistaaHuonenumeronEtunollat() {
        String katuosoite = Katuosoite.builder()
                .lahiosoiteFi("Testaajankatu")
                .katunumero("56")
                .porraskirjain("B")
                .huonenumero("076")
                .build()
                .getAsString("fi");

        assertThat(katuosoite).isEqualTo("Testaajankatu 56 B 76");
    }

}
