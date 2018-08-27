package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ammatti")
@Getter
@NoArgsConstructor
public class Ammatti extends Tietoryhma {

    private String code;
    private String description;

    @Builder
    public Ammatti(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String code, String description) {
        super(ryhmatunnus, muutostapa);
        this.code = code;
        this.description = description;
    }

}
