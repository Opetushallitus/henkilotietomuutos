package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("edunvalvonta_valtuutettu")
@NoArgsConstructor
public class EdunvalvontaValtuutettu extends Tietoryhma {

    /** Mikäli Hetu on tyhjä, annetaan lisäksi Henkilötunnuksettoman Henkilön -tietoryhmä */
    private String hetu;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public EdunvalvontaValtuutettu(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu,
                                   LocalDate startDate, LocalDate endDate) {
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
