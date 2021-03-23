package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenSyntymapaikka;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UlkomainenSyntymapaikkaParserTest {

    private final UlkomainenSyntymapaikkaParser parser = new UlkomainenSyntymapaikkaParser();

    @Test
    public void parsesUlkomainenSyntymapaikka() {
        String tietoryhma = "0091050Dhaka";
        UlkomainenSyntymapaikka syntymapaikka = parser.parse(tietoryhma);
        assertEquals("050", syntymapaikka.getCountryCode());
        assertEquals("Dhaka", syntymapaikka.getLocation());
    }

    @Test
    public void parsesUlkomainenSyntymapaikkaWithAdditionalInformation() {
        String tietoryhma = "0091998Pristina";
        String lisatieto = "4520Kosovo                        ";
        UlkomainenSyntymapaikka syntymapaikka = parser.parse(tietoryhma, lisatieto);
        assertEquals("998", syntymapaikka.getCountryCode());
        assertEquals("Pristina", syntymapaikka.getLocation());
        assertEquals("Kosovo", syntymapaikka.getAdditionalInformation());
    }

    @Test
    public void serializesUlkomainenSyntymapaikka() {
        String tietoryhma = "0091050Dhaka";
        UlkomainenSyntymapaikka syntymapaikka = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(syntymapaikka).trim());
    }

    @Test
    public void serializesUlkomainenSyntymapaikkaWithAdditionalInformation() {
        String tietoryhma = "0091998Pristina                                          ";
        String lisatieto = "4520Kosovo                        ";
        UlkomainenSyntymapaikka syntymapaikka = parser.parse(tietoryhma, lisatieto);
        String expected = String.join("|", tietoryhma, lisatieto);
        assertEquals(expected, parser.serialize(syntymapaikka));
    }

}
