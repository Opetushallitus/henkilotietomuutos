package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Huollonjako;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

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
    protected Set<Muutostapa> getRedundantChanges() {
        return EnumSet.of(Muutostapa.LISATIETO, Muutostapa.KORJATTAVAA);
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (Boolean.TRUE.equals(this.voimassa)
                && HenkiloUpdateUtil.localdateIsBetween(this.startDate, this.endDate, context.getLocalDateNow())
                && !Muutostapa.POISTETTU.equals(getMuutostapa())) {
            // Samaan huoltajaan voi olla useita tietoryhmiä samalla rivillä
            HuoltajaCreateDto huoltajaCreateDto = Optional.ofNullable(henkilo.getHuoltajat())
                    .filter(huoltajat -> !huoltajat.isEmpty())
                    .map(huoltajat -> huoltajat.stream().filter(this::isHuoltajaAlreadyUpdated).findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .orElseGet(HuoltajaCreateDto::new);

            huoltajaCreateDto.setHetu(this.hetu);
            huoltajaCreateDto.setHuoltajuustyyppiKoodi(this.laji);
            if (StringUtils.isEmpty(this.hetu)) {
                huoltajaCreateDto.setEtunimet(this.henkilotunnuksetonHenkilo.getFirstNames());
                huoltajaCreateDto.setSukunimi(this.henkilotunnuksetonHenkilo.getLastname());
                huoltajaCreateDto.setKansalaisuusKoodi(Collections.singleton(this.henkilotunnuksetonHenkilo.getNationality()));
                huoltajaCreateDto.setSyntymaaika(this.henkilotunnuksetonHenkilo.getDateOfBirth());
                huoltajaCreateDto.setHetu(null);
            }
            henkilo.getHuoltajat().add(huoltajaCreateDto);
        }
        else {
            henkilo.getHuoltajat().removeIf(this::isHuoltajaAlreadyUpdated);
        }
    }

    private boolean isHuoltajaAlreadyUpdated(HuoltajaCreateDto huoltajaCreateDto) {
        return Optional.ofNullable(this.hetu)
                .filter(StringUtils::hasLength)
                .map(hetu -> hetu.equals(huoltajaCreateDto.getHetu()))
                .orElseGet(() -> this.henkilotunnuksetonHenkilo != null
                        && this.existAndIsEqual(huoltajaCreateDto.getEtunimet(), this.henkilotunnuksetonHenkilo.getFirstNames())
                        && this.existAndIsEqual(huoltajaCreateDto.getSukunimi(), this.henkilotunnuksetonHenkilo.getLastname()));
    }

    private boolean existAndIsEqual(String a, String b) {
        return Optional.ofNullable(a)
                .filter(StringUtils::hasLength)
                .map(string -> string.equals(b))
                .orElse(false);
    }

}
