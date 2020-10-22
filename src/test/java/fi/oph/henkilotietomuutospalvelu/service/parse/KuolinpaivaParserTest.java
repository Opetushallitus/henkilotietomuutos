package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kuolinpaiva;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class KuolinpaivaParserTest {

    @Test
    public void parsesKuolinpaiva() {
        String tietoryhma = "013120201022";
        Kuolinpaiva kuolinpaiva = KuolinpaivaParser.parseKuolinpaiva(tietoryhma);
        assertEquals(LocalDate.of(2020, 10, 22), kuolinpaiva.getDateOfDeath());
    }

    @Test
    public void serializesKuolinpaiva() {
        String tietoryhma = "013120201022";
        Kuolinpaiva kuolinpaiva = KuolinpaivaParser.parseKuolinpaiva(tietoryhma);
        assertEquals(tietoryhma, KuolinpaivaParser.serializeKuolinpaiva(kuolinpaiva));
    }
}
