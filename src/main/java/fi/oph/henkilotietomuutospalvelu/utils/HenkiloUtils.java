package fi.oph.henkilotietomuutospalvelu.utils;

import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;

import java.util.Optional;

public final class HenkiloUtils {

    private HenkiloUtils() {
    }

    public static final String KIELIKOODI_FI = "fi";
    public static final String KIELIKOODI_SV = "sv";

    public static String getAsiointikieli(HenkiloForceReadDto henkilo) {
        return Optional.ofNullable(henkilo.getAsiointiKieli())
                .flatMap(kieli -> Optional.ofNullable(kieli.getKieliKoodi()))
                .map(kielikoodi -> kielikoodi.trim().toLowerCase())
                .orElse(KIELIKOODI_FI);
    }

    /**
     * Sensuroi hetu. Korvaa loppuosan ja tarkisteen &quot;risuaidoilla&quot;.
     *
     * @param hetu sensuroitava hetu.
     *
     * @return hetu sellaisenaan, jos väärän pituinen tai testihetu.

    public static String sensuroiHetu(String hetu) {
        if (hetu == null || hetu.length() != 11 || hetu.charAt(7) == '9') {
            return hetu;
        }
        return hetu.substring(0, 7) + "####";
    }
     */
}
