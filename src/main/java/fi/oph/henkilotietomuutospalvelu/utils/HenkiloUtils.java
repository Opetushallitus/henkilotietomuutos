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

}
