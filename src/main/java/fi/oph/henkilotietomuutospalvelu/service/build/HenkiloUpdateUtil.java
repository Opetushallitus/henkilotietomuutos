package fi.oph.henkilotietomuutospalvelu.service.build;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class HenkiloUpdateUtil {

    private static final Set<Character> SALLITUT_KUTSUMANIMI_VALIMERKIT = Stream.of(' ', '-').collect(toSet());

    public static boolean isValidKutsumanimi(String etunimet, String kutsumanimi) {
        if (etunimet == null || kutsumanimi == null) {
            return true;
        }

        if("".equals(kutsumanimi)) {
            return false;
        }

        return isValidKutsumanimiCaseSensitive(etunimet.toLowerCase(), kutsumanimi.toLowerCase());
    }

    private static boolean isValidKutsumanimiCaseSensitive(String etunimet, String kutsumanimi) {
        int beginIndex = 0;
        while (beginIndex != -1) {
            beginIndex = etunimet.indexOf(kutsumanimi, beginIndex);
            if (beginIndex != -1) {
                int endIndex = beginIndex + kutsumanimi.length();
                if (isValidBeginIndex(beginIndex, etunimet) && isValidEndIndex(endIndex, etunimet)) {
                    return true;
                }
                beginIndex += 1;
            }
        }
        return false;
    }

    private static boolean isValidBeginIndex(int index, String etunimet) {
        return index == 0 || SALLITUT_KUTSUMANIMI_VALIMERKIT.contains(etunimet.charAt(index - 1));
    }

    private static boolean isValidEndIndex(int index, String etunimet) {
        return index == etunimet.length() || SALLITUT_KUTSUMANIMI_VALIMERKIT.contains(etunimet.charAt(index));
    }

    public static boolean localdateIsBetween(LocalDate start, LocalDate end) {
        return (start == null || LocalDate.now().isAfter(start))
                && (end == null || LocalDate.now().isBefore(end));
    }

    public static boolean localdateExistsAndIsBeforeNow(LocalDate start) {
        return start != null && LocalDate.now().isAfter(start);
    }

    public static boolean localdateNotExistOrIsAfterNow(LocalDate end) {
        return end == null || LocalDate.now().isBefore(end);
    }


}
