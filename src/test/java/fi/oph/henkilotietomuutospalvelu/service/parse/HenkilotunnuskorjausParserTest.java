package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import org.junit.Assert;
import org.junit.Test;

public class HenkilotunnuskorjausParserTest {

    HenkilotunnuskorjausParser parser = new HenkilotunnuskorjausParser();

    @Test
    public void parsesHenkilotunnuskorjaus() {
        String uusiTunnus = "0011060622-451X1";
        String vanhaTunnus = "0013030622-123D2";

        Henkilotunnuskorjaus korjaus = parser.parse(uusiTunnus);
        Assert.assertEquals(Muutostapa.LISATTY, korjaus.getMuutostapa());
        Assert.assertEquals("060622-451X", korjaus.getHetu());
        Assert.assertEquals(true, korjaus.getActive());

        korjaus = parser.parse(vanhaTunnus);
        Assert.assertEquals(Muutostapa.MUUTETTU, korjaus.getMuutostapa());
        Assert.assertEquals("030622-123D", korjaus.getHetu());
        Assert.assertEquals(false, korjaus.getActive());
    }

    @Test
    public void serializesHenkilotunnuskorjaus() {
        String uusiTunnus = "0011060622-451X1";
        Henkilotunnuskorjaus korjaus = parser.parse(uusiTunnus);
        String serialized = parser.serialize(korjaus);
        Assert.assertEquals(uusiTunnus, serialized);
    }

}
