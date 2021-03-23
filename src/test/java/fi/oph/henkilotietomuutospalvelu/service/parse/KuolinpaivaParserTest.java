package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kuolinpaiva;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class KuolinpaivaParserTest {

    KuolinpaivaParser parser = new KuolinpaivaParser();

    @Test
    public void parsesKuolinpaiva() {
        String tietoryhma = "013120201022";
        Kuolinpaiva kuolinpaiva = parser.parse(tietoryhma);
        assertEquals(LocalDate.of(2020, 10, 22), kuolinpaiva.getDateOfDeath());
    }

    @Test
    public void serializesKuolinpaiva() {
        String tietoryhma = "013120201022";
        Kuolinpaiva kuolinpaiva = parser.parse(tietoryhma);
        assertEquals(tietoryhma, parser.serialize(kuolinpaiva));
    }
}
