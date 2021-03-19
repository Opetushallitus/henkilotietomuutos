package fi.oph.henkilotietomuutospalvelu.utils;

import org.junit.Test;

import static fi.oph.henkilotietomuutospalvelu.utils.HenkiloUtils.sensuroiHetu;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HenkiloUtilsTest {

    @Test
    public void sensuroiHetuKorvaaNumeronJaTarkisteen() {
        String hetu = "123456-7890";
        String sensuroitu = "123456-####";
        assertEquals(sensuroitu, sensuroiHetu(hetu));
    }

    @Test
    public void sensuroiHetuPalauttaaNullinSellaisenaan() {
        assertNull(sensuroiHetu(null));
    }

    @Test
    public void sensuroiHetuPalauttaaTestiHetunSellaisenaan() {
        String testiHetu = "123456-999X";
        assertEquals(testiHetu, sensuroiHetu(testiHetu));
    }

}
