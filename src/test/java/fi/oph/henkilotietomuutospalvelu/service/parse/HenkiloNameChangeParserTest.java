package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkiloNameChange;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HenkiloNameChangeParserTest {

    HenkiloNameChangeParser parser = new HenkiloNameChangeParser();

    @Test
    public void parsesHenkiloNameChange() {
        String firstTietoryhmaString = "0051Olli Santeri                                                                                        022017010100000000 ";
        String secondTietoryhmaString = "0053Outi Susanna                                                                                        060000000020170101 ";

        HenkiloNameChange newName = parser.parse(firstTietoryhmaString);
        HenkiloNameChange oldName = parser.parse(secondTietoryhmaString);

        assertEquals(Muutostapa.LISATTY, newName.getMuutostapa());
        assertEquals(LocalDate.of(2017, 1, 1), newName.getStartDate());
        assertNull(newName.getEndDate());

        assertEquals(Muutostapa.MUUTETTU, oldName.getMuutostapa());
        assertNull(oldName.getStartDate());
        assertEquals(LocalDate.of(2017, 1, 1), oldName.getEndDate());

        assertEquals("Olli Santeri", newName.getName());
        assertEquals("Outi Susanna", oldName.getName());
        assertEquals(NameType.ETUNIMI, newName.getNameType());
        assertEquals(NameType.ETUNIMI, oldName.getNameType());
    }

    @Test
    public void serializesHenkiloNameChange() {
        String firstTietoryhmaString = "0051Olli Santeri                                                                                        022017010100000000 ";
        String secondTietoryhmaString = "0053Outi Susanna                                                                                        060000000020170101 ";

        HenkiloNameChange newName = parser.parse(firstTietoryhmaString);
        HenkiloNameChange oldName = parser.parse(secondTietoryhmaString);

        assertEquals(firstTietoryhmaString, parser.serialize(newName));
        assertEquals(secondTietoryhmaString, parser.serialize(oldName));
    }

}
