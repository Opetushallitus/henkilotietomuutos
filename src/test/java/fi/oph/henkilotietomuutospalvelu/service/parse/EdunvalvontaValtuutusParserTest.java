package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.EdunvalvontaValtuutus;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class EdunvalvontaValtuutusParserTest {

    private final EdunvalvontaValtuutusParser parser = new EdunvalvontaValtuutusParser();
    
    @Test
    public void parsesEdunvalvontaValtuutus() {
        String tietoryhma = "31612020102600000000 1 ";
        EdunvalvontaValtuutus valtuutus = parser.parse(tietoryhma);
        assertEquals(LocalDate.of(2020, 10, 26), valtuutus.getStartDate());
        assertFalse(valtuutus.getDutiesStarted());
        assertEquals(Long.valueOf(1), valtuutus.getEdunvalvojaValtuutetut());
        assertNull(valtuutus.getEndDate());
    }

    @Test
    public void serializesEdunvalvontaValtuutus() {
        String tietoryhma = "31612020102600000000 1 ";
        EdunvalvontaValtuutus valtuutus = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(valtuutus));
    }
}
