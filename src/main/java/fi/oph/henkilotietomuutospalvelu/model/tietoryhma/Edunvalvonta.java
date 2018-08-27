package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.dto.type.Toimintakelpoisuus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("edunvalvonta")
@Getter
@NoArgsConstructor
public class Edunvalvonta extends Tietoryhma {

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** Määräys tehtävien jaosta edunvalvojien kesken on annettu. */
    @Column(name = "duties_started")
    private Boolean dutiesStarted;

    @Enumerated(EnumType.STRING)
    private Toimintakelpoisuus edunvalvontatieto;

    private Long edunvalvojat;

    @Builder
    public Edunvalvonta(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa,
                       LocalDate startDate, LocalDate endDate, Boolean dutiesStarted,
                       Toimintakelpoisuus edunvalvontatieto, Long edunvalvojat) {
        super(ryhmatunnus, muutostapa);
        this.startDate = startDate;
        this.endDate = endDate;
        this.dutiesStarted = dutiesStarted;
        this.edunvalvontatieto = edunvalvontatieto;
        this.edunvalvojat = edunvalvojat;
    }

}
