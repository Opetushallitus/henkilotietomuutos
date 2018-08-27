package fi.oph.henkilotietomuutospalvelu.utils;

import lombok.Builder;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Builder
public final class Katuosoite {

    private final String lahiosoiteFi;
    private final String lahiosoiteSv;
    private final String katunumero;
    private final String porraskirjain;
    private final String huonenumero;
    private final String jakokirjain;

    public String getAsString(String kieli) {
        String lahiosoite = HenkiloUtils.KIELIKOODI_SV.equals(kieli) && StringUtils.hasLength(lahiosoiteSv) ? lahiosoiteSv : lahiosoiteFi;
        String huonenumero = StringUtils.trimLeadingCharacter(this.huonenumero, '0');
        return Stream.of(lahiosoite, katunumero, porraskirjain, huonenumero, jakokirjain)
                .map(StringUtils::trimWhitespace)
                .filter(StringUtils::hasLength)
                .collect(joining(" "));
    }

}
