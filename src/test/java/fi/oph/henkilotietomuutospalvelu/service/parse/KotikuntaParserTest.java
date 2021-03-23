package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kotikunta;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class KotikuntaParserTest {

    KotikuntaParser parser = new KotikuntaParser();

    @Test
    public void parsesKotikunta() {
        String tietoryhma = "204112320201022";
        Kotikunta kotikunta = parser.parse(tietoryhma);
        assertEquals("123", kotikunta.getCode());
        assertEquals(LocalDate.of(2020, 10, 22), kotikunta.getMoveDate());
    }

    @Test
    public void serializesKotikunta() {
        String tietoryhma = "204112320201022";
        Kotikunta kotikunta = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(kotikunta));
    }
}
