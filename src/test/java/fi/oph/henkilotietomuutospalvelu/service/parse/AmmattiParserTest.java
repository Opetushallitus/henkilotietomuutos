package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Ammatti;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AmmattiParserTest {

    private final AmmattiParser parser = new AmmattiParser();
    
    @Test
    public void parsesAmmatti() {
        String tietoryhma = "4013    luottoneuvottelija                 ";
        Ammatti ammatti = parser.parse(tietoryhma);
        assertEquals(Muutostapa.MUUTETTU, ammatti.getMuutostapa());
        assertEquals("", ammatti.getCode());
        assertEquals("luottoneuvottelija", ammatti.getDescription());
    }

    @Test
    public void serializesAmmatti() {
        String tietoryhma = "4013    luottoneuvottelija                 ";
        Ammatti ammatti = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(ammatti));
    }

}
