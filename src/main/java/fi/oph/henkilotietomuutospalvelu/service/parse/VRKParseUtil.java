package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.service.exception.TietoryhmaParseException;

import java.time.LocalDate;

public class VRKParseUtil {

    /**
     * Päivämäärät annetaan 8 merkkisinä muodossa vvvvkkpp. Päivämäärät, joiden sisältöä ei ole määrätty (esim. tiedon
     * päättymispäivä tiedon ollessa vielä voimassa), annetaan muodossa 00000000. Myös vanhan järjestelmän
     * peruja olevia vaillinaisia päivämääriä voi esiintyä: esim. 19810100 tai 19810000.
     *
     * Selvästi vaillinaisten päivämäärien (00000000, 99990000, 00000101 tms.) tapauksessa palautetaan null arvo,
     * ja se tarkoittaa, että alku- tai loppupäivä on määrittämätön.
     */
    public static LocalDate deserializeDate(String value) {
        int year = Integer.valueOf(value.substring(0, 4));
        if (year == 0) {
            return null;
        }

        int month = Integer.valueOf(value.substring(4, 6));
        if (month == 0) { month = 1; }

        int day = Integer.valueOf(value.substring(6, 8));
        if (day == 0) { day = 1; }

        return LocalDate.of(year, month, day);
    }

    public static LocalDate parseDateFromHetu(String hetu) throws TietoryhmaParseException {
        Integer day = Integer.valueOf(hetu.substring(0,2));
        Integer month = Integer.valueOf(hetu.substring(2, 4));
        Integer year = getMillenia(hetu) + Integer.valueOf(hetu.substring(4, 6));
        return LocalDate.of(year, month, day);
    }

    private static int getMillenia(String hetu) throws TietoryhmaParseException {
        Character sign = hetu.charAt(6);
        switch(sign) {
            case 'A':
                return 2000;
            case '-':
                return 1900;
            case '+':
                return 1800;
            default:
               throw new TietoryhmaParseException("Invalid sign in hetu " + hetu + "!");
        }
    }

}
