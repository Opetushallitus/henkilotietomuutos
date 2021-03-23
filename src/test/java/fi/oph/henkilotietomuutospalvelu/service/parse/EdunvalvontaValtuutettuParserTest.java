package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.EdunvalvontaValtuutettu;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EdunvalvontaValtuutettuParserTest {

    private final EdunvalvontaValtuutettuParser parser = new EdunvalvontaValtuutettuParser();
    
    @Test
    public void parsesEdunvalvontaValtuutettu() {
        String tietoryhma = "3171111111-99911970010120300101";
        EdunvalvontaValtuutettu valtuutettu = parser.parse(tietoryhma);
        assertEquals("111111-9991", valtuutettu.getHetu());
        assertEquals(LocalDate.of(1970, 1, 1), valtuutettu.getStartDate());
        assertEquals(LocalDate.of(2030, 1, 1), valtuutettu.getEndDate());
        assertNull(valtuutettu.getHenkilotunnuksetonHenkilo());
    }

    @Test
    public void serializesEdunvalvontaValtuutettu() {
        String tietoryhma = "3171111111-99911970010120300101";
        EdunvalvontaValtuutettu valtuutettu = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(valtuutettu));
    }
}
