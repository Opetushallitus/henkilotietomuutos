package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import org.junit.Assert;
import org.junit.Test;

public class HenkilotunnuskorjausParserTest {

    @Test
    public void parsesHenkilotunnuskorjaus() {
        String uusiTunnus = "0011060622-451X1";
        String vanhaTunnus = "0013030622-123D2";

        Henkilotunnuskorjaus korjaus = HenkilotunnuskorjausParser.parseHenkilotunnuskorjaus(uusiTunnus);
        Assert.assertEquals(Muutostapa.LISATTY, korjaus.getMuutostapa());
        Assert.assertEquals("060622-451X", korjaus.getHetu());
        Assert.assertEquals(true, korjaus.getActive());

        korjaus = HenkilotunnuskorjausParser.parseHenkilotunnuskorjaus(vanhaTunnus);
        Assert.assertEquals(Muutostapa.MUUTETTU, korjaus.getMuutostapa());
        Assert.assertEquals("030622-123D", korjaus.getHetu());
        Assert.assertEquals(false, korjaus.getActive());
    }

    @Test
    public void serializesHenkilotunnuskorjaus() {
        String uusiTunnus = "0011060622-451X1";
        Henkilotunnuskorjaus korjaus = HenkilotunnuskorjausParser.parseHenkilotunnuskorjaus(uusiTunnus);
        String serialized = HenkilotunnuskorjausParser.serializeHenkilotunnuskorjaus(korjaus);
        Assert.assertEquals(uusiTunnus, serialized);
    }

}
