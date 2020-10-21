package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Aidinkieli;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AidinkieliParserTest {

    @Test
    public void parseAidinkieliWhenLanguageCodeIsValid() {
        String tietoryhmaStr = "0021fi";
        Aidinkieli aidinkieli = AidinkieliParser.parseAidinkieli(tietoryhmaStr);
        assertEquals("fi", aidinkieli.getLanguageCode());
    }

    @Test
    public void parseAidinkieliWhenLanguageCodeContainsAdditionalInformation() {
        String tietoryhmaStr = "002198";
        String lisatiedot = "4520suomalainen viittomakieli     ";
        Aidinkieli aidinkieli = AidinkieliParser.parseAidinkieli(tietoryhmaStr, lisatiedot);
        assertEquals("98", aidinkieli.getLanguageCode());
        assertEquals("suomalainen viittomakieli", aidinkieli.getAdditionalInformation());
    }

    @Test
    public void parseAidinkieliSkipsMissingAdditionalInformation() {
        String tietoryhmaStr = "002198";
        Aidinkieli aidinkieli = AidinkieliParser.parseAidinkieli(tietoryhmaStr);
        assertEquals("98", aidinkieli.getLanguageCode());
    }

    @Test
    public void serializesAidinkieli() {
        String tietoryhmaStr = "0021fi";
        Aidinkieli aidinkieli = AidinkieliParser.parseAidinkieli(tietoryhmaStr);
        String serialized = AidinkieliParser.serializeAidinkieli(aidinkieli);
        assertEquals(tietoryhmaStr, serialized);
    }

    @Test
    public void serializesAidinkieliWithAdditionalInformation() {
        String tietoryhmaStr = "002198";
        String lisatiedot = "4520suomalainen viittomakieli     ";
        Aidinkieli aidinkieli = AidinkieliParser.parseAidinkieli(tietoryhmaStr, lisatiedot);
        String expected = String.join("|", tietoryhmaStr, lisatiedot);
        String serialized = AidinkieliParser.serializeAidinkieli(aidinkieli);
        assertEquals(expected, serialized);
    }

}
