package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkiloName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HenkiloNameParserTest {

    @Test
    public void parsesHenkiloName() {
        String firstTietoryhmaString = "0045Miehenä Tes                                                                                         Ukko Antto                                                                                          00000000 ";
        String secondTietoryhmaString = "0046Miehenä Tes                                                                                         Ukko Anton                                                                                          00000000 ";
        HenkiloName oldName = HenkiloNameParser.parseHenkiloName(firstTietoryhmaString);
        HenkiloName newName = HenkiloNameParser.parseHenkiloName(secondTietoryhmaString);

        assertEquals(Muutostapa.KORJATTAVAA, oldName.getMuutostapa());
        assertNull(newName.getLastUpdateDate());

        assertEquals(Muutostapa.KORJATTU, newName.getMuutostapa());
        assertNull(newName.getLastUpdateDate());

        assertEquals("Ukko Anton", newName.getFirstNames());
        assertEquals("Ukko Antto", oldName.getFirstNames());
        assertEquals("Miehenä Tes", newName.getLastName());
        assertEquals("Miehenä Tes", oldName.getLastName());
    }

    @Test
    public void serializesHenkiloName() {
        String firstTietoryhmaString = "0045Miehenä Tes                                                                                         Ukko Antto                                                                                          00000000 ";
        String secondTietoryhmaString = "0046Miehenä Tes                                                                                         Ukko Anton                                                                                          00000000 ";
        HenkiloName oldName = HenkiloNameParser.parseHenkiloName(firstTietoryhmaString);
        HenkiloName newName = HenkiloNameParser.parseHenkiloName(secondTietoryhmaString);
        assertEquals(firstTietoryhmaString, HenkiloNameParser.serializeHenkiloName(oldName));
        assertEquals(secondTietoryhmaString, HenkiloNameParser.serializeHenkiloName(newName));
    }

}
