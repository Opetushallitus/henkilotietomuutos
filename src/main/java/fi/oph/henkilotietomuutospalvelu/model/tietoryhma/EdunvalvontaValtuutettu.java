package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvontaValtuutettuParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("edunvalvonta_valtuutettu")
@NoArgsConstructor
@Getter
public class EdunvalvontaValtuutettu extends Tietoryhma {

    /** Mikäli Hetu on tyhjä, annetaan lisäksi Henkilötunnuksettoman Henkilön -tietoryhmä */
    private String hetu;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hetuton_henkilo")
    private HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo;

    @Builder
    public EdunvalvontaValtuutettu(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu,
                                   LocalDate startDate, LocalDate endDate, HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo) {
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        this.startDate = startDate;
        this.endDate = endDate;
        this.henkilotunnuksetonHenkilo = henkilotunnuksetonHenkilo;
    }
}
