package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("syntyma_kotikunta")
@NoArgsConstructor
public class SyntymaKotikunta extends Tietoryhma {

    private String kuntakoodi; // kolmenumeroinen

    @Builder
    public SyntymaKotikunta(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String kuntakoodi) {
        super(ryhmatunnus, muutostapa);
        this.kuntakoodi = kuntakoodi;
    }

}
