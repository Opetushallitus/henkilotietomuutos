package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoAlkupera;
import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static fi.oph.henkilotietomuutospalvelu.utils.YhteystietoUtils.removeYhteystietoryhma;

@NoArgsConstructor
public abstract class YhteystietoTietoryhma extends Tietoryhma {

    private static final Set<Muutostapa> REDUNDANT_CHANGES = EnumSet
            .of(Muutostapa.LISATIETO, Muutostapa.KORJATTAVAA);
    protected static final String ISO3166_FI = "246";

    public YhteystietoTietoryhma(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa) {
        super(ryhmatunnus, muutostapa);
    }

    @Override
    public boolean isVoimassa() {
        LocalDate startDate = getStartDate();
        LocalDate endDate = getEndDate();
        LocalDate now = LocalDate.now();
        boolean startOk = startDate == null || now.isAfter(startDate) || now.isEqual(startDate);
        boolean endOk = endDate == null || now.isBefore(endDate) || now.isEqual(endDate);
        return startOk && endOk;
    }

    @Override
    protected final Set<Muutostapa> getRedundantChanges() {
        return REDUNDANT_CHANGES;
    }

    @Override
    protected final void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        KoodistoYhteystietoAlkupera alkupera = getAlkupera();
        KoodistoYhteystietoTyyppi tyyppi = getTyyppi();

        removeYhteystietoryhma(henkilo.getYhteystiedotRyhma(), alkupera, tyyppi);
        if (!Muutostapa.POISTETTU.equals(getMuutostapa()) && isVoimassa()) {
            YhteystiedotRyhmaDto yhteystietoryhma = new YhteystiedotRyhmaDto();
            yhteystietoryhma.setReadOnly(true);
            yhteystietoryhma.setRyhmaAlkuperaTieto(alkupera.getKoodi());
            yhteystietoryhma.setRyhmaKuvaus(tyyppi.getKoodi());
            yhteystietoryhma.setYhteystieto(new HashSet<>());
            henkilo.getYhteystiedotRyhma().add(yhteystietoryhma);
            updateYhteystietoryhma(context, yhteystietoryhma);
        }
    }

    protected KoodistoYhteystietoAlkupera getAlkupera() {
        return KoodistoYhteystietoAlkupera.VTJ;
    }

    protected abstract KoodistoYhteystietoTyyppi getTyyppi();

    protected abstract LocalDate getStartDate();

    protected abstract LocalDate getEndDate();

    protected abstract void updateYhteystietoryhma(Context context, YhteystiedotRyhmaDto yhteystietoryhma);

}
