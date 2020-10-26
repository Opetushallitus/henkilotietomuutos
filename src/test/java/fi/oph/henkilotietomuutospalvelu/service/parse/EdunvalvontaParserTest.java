package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Toimintakelpoisuus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Edunvalvonta;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EdunvalvontaParserTest {

    @Test
    public void parseEdunvalvonta() {
        String tietoryhma = "30601989020100000000 102|3075                    0000000001989020119980101";
        Edunvalvonta valvonta = EdunvalvontaParser.parseEdunvalvonta(tietoryhma);
        assertEquals(Muutostapa.LISATIETO, valvonta.getMuutostapa());
        assertEquals(LocalDate.of(1989, 2, 1), valvonta.getStartDate());
        assertNull(valvonta.getEndDate());
        assertEquals(false, valvonta.getDutiesStarted());
        assertEquals(Toimintakelpoisuus.RAJOITTAMATON, valvonta.getEdunvalvontatieto());
        assertEquals(Long.valueOf(2), valvonta.getEdunvalvojat());
    }

}
