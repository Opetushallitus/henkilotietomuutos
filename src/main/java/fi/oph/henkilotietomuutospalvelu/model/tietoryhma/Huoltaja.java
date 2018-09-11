package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Huollonjako;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("huoltaja")
@Getter
@NoArgsConstructor
public class Huoltaja extends Tietoryhma {

    /** Mikäli Hetu on tyhjä, annetaan lisäksi Henkilötunnuksettoman Henkilön -tietoryhmä */
    private String hetu;

    /**
     * Henkilösuhteen lajia kuvaava koodi.
     * 03 = vanhempi huoltajana
     * 36 = vanhempi määräyksenvaraisena huoltajana
     * 06 = muu huoltaja
     */
    private String laji;
    // 0, 1, 2,... in DB
    private Huollonjako huollonjako;
    private Boolean voimassa;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "resolution_date")
    private LocalDate resolutionDate;

    @Builder
    public Huoltaja(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu, String laji,
                    Huollonjako huollonjako, Boolean voimassa, LocalDate startDate, LocalDate endDate, LocalDate resolutionDate) {
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        this.laji = laji;
        this.huollonjako = huollonjako;
        this.voimassa = voimassa;
        this.startDate = startDate;
        this.endDate = endDate;
        this.resolutionDate = resolutionDate;
    }

}
