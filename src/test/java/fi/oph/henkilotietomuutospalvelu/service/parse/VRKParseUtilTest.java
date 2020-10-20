package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.service.exception.TietoryhmaParseException;
import fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class VRKParseUtilTest {

    @Test
    public void deserializeLocalDateWhenDateStringIsValid() {
        String dateString = "19890814"; // VVVVKKPP
        LocalDate date = VRKParseUtil.deserializeDate(dateString);

        Assert.assertEquals(LocalDate.of(1989, 8, 14), date);
    }

    @Test
    public void deserializeLocalDateWhenDateStringIsAllZeros() {
        String dateString = "00000000";
        LocalDate date = VRKParseUtil.deserializeDate(dateString);

        Assert.assertNull(date);
    }

    @Test
    public void deserializeLocalDateWhenDateIsPartiallyValid() {
        String dateString = "19810100";
        String otherDateString = "19810000";

        LocalDate date = VRKParseUtil.deserializeDate(dateString);
        Assert.assertEquals(LocalDate.of(1981, 1, 1), date);

        LocalDate otherDate = VRKParseUtil.deserializeDate(otherDateString);
        Assert.assertEquals(LocalDate.of(1981, 1, 1), otherDate);
    }

    @Test
    public void serializeNullLocalDate() {
        Assert.assertEquals(VRKParseUtil.UNDEFINED_DATE, VRKParseUtil.serializeDate(null));
    }

    @Test
    public void serializeValidLocalDate() {
        String dateString = "20120524";
        LocalDate date = VRKParseUtil.deserializeDate(dateString);
        String serialized = VRKParseUtil.serializeDate(date);
        Assert.assertEquals(dateString, serialized);
    }

    @Test
    public void parseDateFrom1900Hetu() {
        String hetu = "120212-112X";

        LocalDate date = VRKParseUtil.parseDateFromHetu(hetu);
        Assert.assertEquals(LocalDate.of(1912, 2, 12), date);
    }

    @Test
    public void parseDateFrom2000Hetu() {
        String hetu = "130901A345X";

        LocalDate date = VRKParseUtil.parseDateFromHetu(hetu);
        Assert.assertEquals(LocalDate.of(2001, 9,13), date);
    }

    @Test
    public void parseDateFrom1800Hetu() {
        String hetu = "230698+983X";

        LocalDate date = VRKParseUtil.parseDateFromHetu(hetu);
        Assert.assertEquals(LocalDate.of(1898, 6, 23), date);
    }

    @Test(expected = TietoryhmaParseException.class)
    public void parseInvalidHetu() {
        String hetu = "230592X234E";
        VRKParseUtil.parseDateFromHetu(hetu);
    }

}
