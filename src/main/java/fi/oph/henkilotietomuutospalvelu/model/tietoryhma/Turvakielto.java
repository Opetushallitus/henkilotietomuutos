package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("turvakielto")
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class Turvakielto extends Tietoryhma {

    /** Turvakiellon alkupäivä on sama kuin tunnisteosassa annettava muutospäivä. */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** Päättymispäivä on null, jos turvakielto on toistaiseksi voimassa. */
    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public Turvakielto(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, LocalDate endDate) {
        super(ryhmatunnus, muutostapa);
        this.endDate = endDate;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        // We don't schedule upcoming updates se we ignore startDate
        if (this.endDate == null || LocalDate.now().isBefore(this.endDate)) {
            henkilo.setTurvakielto(true);
        }
        else {
            henkilo.setTurvakielto(false);
        }
    }
}
