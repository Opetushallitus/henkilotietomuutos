package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.KotimainenOsoite;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.TilapainenKotimainenOsoite;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class KotimainenOsoiteParserTest {
//Humikkalabacken
//Humikkalabrinken
    @Test
    public void parsesKotimainenOsoite() {
        String tietoryhma = "1011Humikkalanrinne                                                                                     Humikkalabrinken                                                                                    1           009402019102600000000";
        KotimainenOsoite osoite = KotimainenOsoiteParser.parseKotimainenOsoite(tietoryhma);
        assertEquals(Ryhmatunnus.KOTIMAINEN_OSOITE, osoite.getRyhmatunnus());
        assertEquals("Humikkalanrinne", osoite.getLahiosoite());
        assertEquals("Humikkalabrinken", osoite.getLahiosoiteSV());
        assertEquals("1", osoite.getKatunumero());
        assertEquals("00940", osoite.getPostinumero());
        assertEquals(LocalDate.of(2019, 10, 26), osoite.getStartDate());
        assertNull(osoite.getEndDate());
    }

    @Test
    public void parsesTilapainenKotimainenOsoite() {
        String tietoryhma = "1021Humikkalanrinne                                                                                     Humikkalabrinken                                                                                    1           009402019102600000000";
        KotimainenOsoite osoite = KotimainenOsoiteParser.parseKotimainenOsoite(tietoryhma);
        assertEquals(Ryhmatunnus.KOTIMAINEN_OSOITE_TILAPAINEN, osoite.getRyhmatunnus());
    }

    @Test
    public void serializesKotimainenOsoite() {
        String tietoryhma = "1011Humikkalanrinne                                                                                     Humikkalabrinken                                                                                    1           009402019102600000000";
        KotimainenOsoite osoite = KotimainenOsoiteParser.parseKotimainenOsoite(tietoryhma);
        assertEquals(tietoryhma, KotimainenOsoiteParser.serializeKotimainenOsoite(osoite));
    }

    @Test
    public void serializesTilapainenKotimainenOsoite() {
        String tietoryhma = "1021Humikkalanrinne                                                                                     Humikkalabrinken                                                                                    1           009402019102600000000";
        TilapainenKotimainenOsoite osoite = TilapainenKotimainenOsoite.from(
                KotimainenOsoiteParser.parseKotimainenOsoite(tietoryhma));
        assertEquals(tietoryhma, KotimainenOsoiteParser.serializeKotimainenOsoite(osoite));
    }
}
