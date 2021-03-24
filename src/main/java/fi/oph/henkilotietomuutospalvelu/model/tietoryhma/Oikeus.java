package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * Huoltajan tiedonsaanti- tai päätösoikeus.
 */
@Entity
@DiscriminatorValue("oikeus")
@Getter
@Setter
@NoArgsConstructor
public class Oikeus extends Tietoryhma {

    /**
     * P, T tai A + 3 numeroa
     * P = päätösoikeus
     * T = tiedonsaantioikeus
     * A = päätös/sopimusvuoroasumisesta
     */
    private String koodi;

    @Column(name = "start_date")
    private LocalDate alkupvm;

    @Column(name = "end_date")
    private LocalDate loppupvm;

    @Builder
    public Oikeus(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String koodi, LocalDate alkupvm, LocalDate loppupvm) {
        super(ryhmatunnus, muutostapa);
        this.koodi = koodi;
        this.alkupvm = alkupvm;
        this.loppupvm = loppupvm;
    }

}
