package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
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
public class Sukupuoli extends Tietoryhma {

    private Gender gender;

    @Builder
    public Sukupuoli(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, Gender gender){
        super(ryhmatunnus, muutostapa);
        this.gender = gender;
    }


    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        henkilo.setSukupuoli(this.gender.getCode());
    }
}
