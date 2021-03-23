package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.SukupuoliParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("sukupuoli")
@Getter
@Setter
@NoArgsConstructor
public class Sukupuoli extends Tietoryhma<Sukupuoli> {

    private static final SukupuoliParser PARSER = new SukupuoliParser();

    private Gender gender;

    @Builder
    public Sukupuoli(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, Gender gender){
        super(ryhmatunnus, muutostapa);
        this.gender = gender;
    }

    @Override
    protected Sukupuoli getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<Sukupuoli> getParser() {
        return PARSER;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        henkilo.setSukupuoli(this.gender.getCode());
    }
}
