package fi.oph.henkilotietomuutospalvelu.utils;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoAlkupera;
import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class YhteystietoUtils {

    private YhteystietoUtils() {
    }

    public static Optional<YhteystiedotRyhmaDto> findYhteystietoryhma(Set<YhteystiedotRyhmaDto> yhteystietoryhmat,
            KoodistoYhteystietoAlkupera alkupera, KoodistoYhteystietoTyyppi tyyppi) {
        return yhteystietoryhmat.stream()
                .filter(new YhteystietoryhmaPredicate(alkupera, tyyppi))
                .findFirst();
    }

    public static boolean removeYhteystietoryhma(Set<YhteystiedotRyhmaDto> yhteystietoryhmat,
            KoodistoYhteystietoAlkupera alkupera, KoodistoYhteystietoTyyppi tyyppi) {
        return yhteystietoryhmat.removeIf(new YhteystietoryhmaPredicate(alkupera, tyyppi));
    }

    public static boolean removeYhteystietoryhma(Set<YhteystiedotRyhmaDto> yhteystietoryhmat,
                                                 KoodistoYhteystietoAlkupera alkupera) {
        return yhteystietoryhmat.removeIf(yhteystietoryhma
                -> alkupera.getKoodi().equals(yhteystietoryhma.getRyhmaAlkuperaTieto()));
    }

    public static Optional<YhteystietoDto> findYhteystieto(YhteystiedotRyhmaDto yhteystietoryhma,
            YhteystietoTyyppi tyyppi) {
        return yhteystietoryhma.getYhteystieto().stream()
                .filter(yhteystieto -> tyyppi.equals(yhteystieto.getYhteystietoTyyppi()))
                .findFirst();
    }

    public static void setYhteystietoArvo(YhteystiedotRyhmaDto yhteystietoryhma,
            YhteystietoTyyppi tyyppi, String arvo) {
        findYhteystieto(yhteystietoryhma, tyyppi).orElseGet(() -> {
            YhteystietoDto yhteystieto = new YhteystietoDto();
            yhteystieto.setYhteystietoTyyppi(tyyppi);
            yhteystietoryhma.getYhteystieto().add(yhteystieto);
            return yhteystieto;
        }).setYhteystietoArvo(arvo != null ? arvo : "");// oppijanumerorekisterissä null = arvo pysyy ennallaan
    }

    @RequiredArgsConstructor
    private static class YhteystietoryhmaPredicate implements Predicate<YhteystiedotRyhmaDto> {

        private final KoodistoYhteystietoAlkupera alkupera;
        private final KoodistoYhteystietoTyyppi tyyppi;

        @Override
        public boolean test(YhteystiedotRyhmaDto yhteystietoryhma) {
            return alkupera.getKoodi().equals(yhteystietoryhma.getRyhmaAlkuperaTieto())
                    && tyyppi.getKoodi().equals(yhteystietoryhma.getRyhmaKuvaus());
        }

    }

}
