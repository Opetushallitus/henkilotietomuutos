package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.UlkomainenSyntymapaikka;
import org.junit.Test;

import static fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenSyntymapaikkaParser.parseUlkomainenSyntymapaikka;
import static fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenSyntymapaikkaParser.serializeUlkomainenSyntymapaikka;
import static org.junit.Assert.assertEquals;

public class UlkomainenSyntymapaikkaParserTest {

    @Test
    public void parsesUlkomainenSyntymapaikka() {
        String tietoryhma = "0091050Dhaka";
        UlkomainenSyntymapaikka syntymapaikka = parseUlkomainenSyntymapaikka(tietoryhma);
        assertEquals("050", syntymapaikka.getCountryCode());
        assertEquals("Dhaka", syntymapaikka.getLocation());
    }

    @Test
    public void parsesUlkomainenSyntymapaikkaWithAdditionalInformation() {
        String tietoryhma = "0091998Pristina";
        String lisatieto = "4521Kosovo                        ";
        UlkomainenSyntymapaikka syntymapaikka = parseUlkomainenSyntymapaikka(tietoryhma, lisatieto);
        assertEquals("998", syntymapaikka.getCountryCode());
        assertEquals("Pristina", syntymapaikka.getLocation());
        assertEquals("Kosovo", syntymapaikka.getAdditionalInformation());
    }

    @Test
    public void serializesUlkomainenSyntymapaikka() {
        String tietoryhma = "0091050Dhaka";
        UlkomainenSyntymapaikka syntymapaikka = parseUlkomainenSyntymapaikka(tietoryhma);
        assertEquals(tietoryhma, serializeUlkomainenSyntymapaikka(syntymapaikka).trim());
    }

    @Test
    public void serializesUlkomainenSyntymapaikkaWithAdditionalInformation() {
        String tietoryhma = "0091998Pristina                                          ";
        String lisatieto = "4521Kosovo                        ";
        UlkomainenSyntymapaikka syntymapaikka = parseUlkomainenSyntymapaikka(tietoryhma, lisatieto);
        String expected = String.join("|", tietoryhma, lisatieto);
        assertEquals(expected, serializeUlkomainenSyntymapaikka(syntymapaikka));
    }

}
