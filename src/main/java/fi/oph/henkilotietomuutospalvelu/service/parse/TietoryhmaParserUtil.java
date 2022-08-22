package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.service.exception.TietoryhmaParseException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

@Slf4j
public class TietoryhmaParserUtil {

    public static Tietoryhma deserializeTietoryhma(String tietoryhma, String... tarkentavatTietoryhmat)
            throws TietoryhmaParseException {
        if (tietoryhma.length() < 4) {
            throw new TietoryhmaParseException("Tietoryhmä has a length less than 4 and is not valid!");
        }
        try {
            String ryhmakoodi = parseRyhmatunnus(tietoryhma);
            Ryhmatunnus ryhmatunnus = Ryhmatunnus.getEnum(ryhmakoodi);
            if (ryhmatunnus == null) {
                throw new TietoryhmaParseException("Unsupported Tietoryhma! Ryhmatunnus: " + ryhmakoodi);
            }
            TietoryhmaParser<?> parser = ryhmatunnus.getParser();
            return parser != null ? parser.parse(tietoryhma, tarkentavatTietoryhmat) : null;
        } catch (RuntimeException e) {
            if (e instanceof TietoryhmaParseException) {
                throw e;
            }
            throw new TietoryhmaParseException("Deserializing Tietoryhma failed!", e);
        }
    }

    @NotNull
    public static String parseRyhmatunnus(String tietoryhma) {
        return parseString(tietoryhma, 0, 3);
    }

    static String parseAdditionalInformation(String value) {
        if (value == null) {
            throw new TietoryhmaParseException("Additional information was null!");
        }
        return value.substring(4).trim();
    }

    static String serializeAdditionalInformation(String information) {
        return Ryhmatunnus.LISATIETO.getCode()
                + Muutostapa.LISATIETO.getNumber()
                + serializeString(information, 30);
    }

    static Muutostapa parseMuutosTapa(String value) {
        Long number = Long.valueOf(value.substring(3, 4));
        return Muutostapa.get(number);
    }

    static String parseCharacter(String str, int startIndex) {
        return str.substring(startIndex, startIndex+1);
    }

    static String parseString(String str, int startIndex, int length) {
        try {
            int endIndex = startIndex + length;
            int strLength = str.length();
            if (strLength < endIndex) {
                endIndex = strLength;
            }
            return str.substring(startIndex, endIndex).trim();
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Failed to parse string '{}':", str, e);
            throw e;
        }
    }

    static LocalDate parseDate(String str, int startIndex) {
        return VRKParseUtil.deserializeDate(parseString(str, startIndex, 8));
    }
}
