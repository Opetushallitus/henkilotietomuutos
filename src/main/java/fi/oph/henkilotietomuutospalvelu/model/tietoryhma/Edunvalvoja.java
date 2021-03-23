package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvojaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("edunvalvoja")
@Getter
@NoArgsConstructor
public class Edunvalvoja extends Tietoryhma {

    private static final EdunvalvojaParser PARSER = new EdunvalvojaParser();
    /** Mikäli Hetu on tyhjä, annetaan lisäksi Henkilötunnuksettoman Henkilön -tietoryhmä */
    private String hetu;
    @Column(name = "y_tunnus")
    private String yTunnus;
    @Column(name = "municipality_code")
    private String municipalityCode; // Kolminumeroinen kuntakoodi

    /**
     * Valtion oikeusaputoimistoilla, jotka hoitavat edunvalvontatehtäviä on oma 3-numeroinen yksikkökoodinsa.
     * Muutostietopalvelussa yksikkökoodit täydennetään 6-numeroisiksi kolmella nollalla (esim. 000374).
     * Yksikkökoodeja vastaavat oikeusaputoimistojen nimet toimitetaan muutostietopalveluasiakkaille.
     */
    @Column(name = "oikeusaputoimisto_koodi")
    private String oikeusaputoimistoKoodi;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hetuton_henkilo")
    private HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo;

    @Builder
    public Edunvalvoja(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu, String yTunnus,
                       String municipalityCode, String oikeusaputoimistoKoodi, LocalDate startDate, LocalDate endDate,
                       HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo) {
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        this.yTunnus = yTunnus;
        this.municipalityCode = municipalityCode;
        this.oikeusaputoimistoKoodi = oikeusaputoimistoKoodi;
        this.startDate = startDate;
        this.endDate = endDate;
        this.henkilotunnuksetonHenkilo = henkilotunnuksetonHenkilo;
    }
}
