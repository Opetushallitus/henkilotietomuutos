package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KansalaisuusDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toSet;

@Entity
@DiscriminatorValue("kansalaisuus")
@Getter
@Slf4j
@NoArgsConstructor
public class Kansalaisuus extends Tietoryhma {

    /**
     * Kansalaisuudet valtiokoodiston (ISO3166-standardi) mukaisina tai sitten selväkielisenä tietona.
     * Selväkielisen tiedon sisältävä tietoryhmä seuraa, jos koodi on 998.
     * Kansalaisuustietoryhmien lajittelujärjestys: muutosattribuutti, saamispäivä.
     */

    private String code;
    private Boolean valid;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public Kansalaisuus(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String code, Boolean valid,
                        LocalDate startDate, LocalDate endDate){
        super(ryhmatunnus, muutostapa);
        this.code = code;
        this.valid = valid;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected Set<Muutostapa> getRedundantChanges() {
        return EnumSet.of(Muutostapa.LISATIETO, Muutostapa.KORJATTAVAA);
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        // We don't schedule upcoming updates so startDate is ignored
        boolean voimassa = this.valid && (this.endDate == null || context.getLocalDateNow().isBefore(this.endDate));

        Set<String> kansalaisuudet = Optional.ofNullable(henkilo.getKansalaisuus())
                .orElseGet(() -> context.getCurrentHenkilo().getKansalaisuus())
                .stream().map(KansalaisuusDto::getKansalaisuusKoodi).collect(toSet());

        BiFunction<Collection, String, Boolean> updateFunc = getUpdateFunc(voimassa);
        if (updateFunc != null && updateFunc.apply(kansalaisuudet, code)) {
            henkilo.setKansalaisuus(kansalaisuudet
                    .stream()
                    .map(koodi -> {
                        KansalaisuusDto kansalaisuusDto = new KansalaisuusDto();
                        kansalaisuusDto.setKansalaisuusKoodi(koodi);
                        return kansalaisuusDto;
                    })
                    .collect(toSet()));
        }
    }

    private BiFunction<Collection, String, Boolean> getUpdateFunc(boolean voimassa) {
        switch (getMuutostapa()) {
            case LISATTY:
            case MUUTETTU:
            case KORJATTU:
                if (voimassa) {
                    return Collection::add;
                } else {
                    return Collection::remove;
                }
            case POISTETTU:
                return Collection::remove;
            default:
                throw new IllegalArgumentException("Tuntematon muutostapa: " + getMuutostapa());
        }
    }

}
