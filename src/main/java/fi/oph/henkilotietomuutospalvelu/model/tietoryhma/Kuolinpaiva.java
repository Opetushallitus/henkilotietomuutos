package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.KuolinpaivaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("kuolinpaiva")
@Getter
@NoArgsConstructor
public class Kuolinpaiva extends Tietoryhma<Kuolinpaiva> {

    private static final KuolinpaivaParser PARSER = new KuolinpaivaParser();

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    @Builder
    public Kuolinpaiva(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, LocalDate dateOfDeath) {
        super(ryhmatunnus, muutostapa);
        this.dateOfDeath = dateOfDeath;
    }

    @Override
    protected Kuolinpaiva getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<Kuolinpaiva> getParser() {
        return PARSER;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        henkilo.setKuolinpaiva(this.dateOfDeath);
    }
}
