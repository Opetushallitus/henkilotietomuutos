package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Huollonjako;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloCreateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Slf4j
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hetuton_henkilo")
    private HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo;

    @Builder
    public Huoltaja(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu, String laji,
                    Huollonjako huollonjako, Boolean voimassa, LocalDate startDate, LocalDate endDate, LocalDate resolutionDate,
                    HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo) {
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        // Huoltajuustyyppi koodisto
        this.laji = laji;
        this.huollonjako = huollonjako;
        this.voimassa = voimassa;
        this.startDate = startDate;
        this.endDate = endDate;
        this.resolutionDate = resolutionDate;
        this.henkilotunnuksetonHenkilo = henkilotunnuksetonHenkilo;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (Boolean.TRUE.equals(this.voimassa)
                && HenkiloUpdateUtil.localdateIsBetween(this.startDate, this.endDate)) {
            HuoltajaCreateDto huoltajaCreateDto = new HuoltajaCreateDto();
            huoltajaCreateDto.setHetu(this.hetu);
            huoltajaCreateDto.setHuoltajuustyyppiKoodi(this.laji);
            if (StringUtils.isEmpty(this.hetu)) {
                huoltajaCreateDto.setEtunimet(this.henkilotunnuksetonHenkilo.getFirstNames());
                huoltajaCreateDto.setSukunimi(this.henkilotunnuksetonHenkilo.getLastname());
                huoltajaCreateDto.setKansalaisuusKoodi(Collections.singleton(this.henkilotunnuksetonHenkilo.getNationality()));
                huoltajaCreateDto.setSyntymaaika(this.henkilotunnuksetonHenkilo.getDateOfBirth());
            }
            henkilo.getHuoltajat().add(huoltajaCreateDto);
        }
        else {
            log.warn("Huoltajuus ei voimassa. Ei käsitellä.");
        }
    }

}
