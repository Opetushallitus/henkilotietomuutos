package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TurvakieltoParser;
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
import java.util.EnumSet;
import java.util.Set;

@Entity
@DiscriminatorValue("turvakielto")
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class Turvakielto extends Tietoryhma<Turvakielto> {

    private static final TurvakieltoParser PARSER = new TurvakieltoParser();

    private static final EnumSet<Muutostapa> REDUNDANT_CHANGES =
            EnumSet.of(Muutostapa.LISATIETO, Muutostapa.KORJATTAVAA);

    /** Päättymispäivä on null, jos turvakielto on toistaiseksi voimassa. */
    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public Turvakielto(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, LocalDate endDate) {
        super(ryhmatunnus, muutostapa);
        this.endDate = endDate;
    }

    @Override
    protected Turvakielto getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<Turvakielto> getParser() {
        return PARSER;
    }

    @Override
    protected Set<Muutostapa> getRedundantChanges() {
        return REDUNDANT_CHANGES;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (Muutostapa.POISTETTU.equals(getMuutostapa())) {
            henkilo.setTurvakielto(false);
            return;
        }
        if (this.endDate == null || context.getLocalDateNow().isBefore(this.endDate)) {
            henkilo.setTurvakielto(true);
        }
        else {
            henkilo.setTurvakielto(false);
        }
    }
}
