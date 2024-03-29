package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

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
import java.util.*;

@Slf4j
@Entity
@DiscriminatorValue("huoltaja")
@Getter
@NoArgsConstructor
public class Huoltaja extends Tietoryhma {

    /** Mikäli Hetu on tyhjä, annetaan lisäksi Henkilötunnuksettoman Henkilön -tietoryhmä */
    private String hetu;

    /**
     * Huoltajan laji.
     * 1 = vanhempi,
     * 2 = päätetty/oheis/muu huoltaja,
     * 3 = tiedonsaantioikeutettu (eli ei ole virallinen huoltaja, esim. isä jolta on huolto päättynyt tai muu huoltaja)
     */
    @Column(name = "laji")
    private String laji;

    /**
     * Huoltajan rooli.
     * arvot null, 1 – 4, 6
     * null = muu huoltaja/tiedonsaanti- tai päätösoikeutettu, joka ei ole varsinainen huoltaja
     * 1 = isä
     * 2 = äiti
     * 3 = adoptioisä
     * 4 = adoptioäiti
     * 6 = vahvistettu äiti
     */
    private String rooli;

    /**
     * Huoltosuhteen alkamispäivä.
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Huoltosuhteen päättymispäivä.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Huollettavan asuminen.
     * tyhjä = kentän arvo on tyhjää, jos vuoroasumisesta ei ole tehty päätöstä
     * 1 = lapsi asuu vanhempiensa luona (isä ja äiti / äiti ja äiti / isä ja isä / adoptioisä ja adoptioäiti / isä ja adoptiovanhempi / äiti ja adoptiovanhempi)
     * 2 = äidin luona, oikeuden päätös tai yhteisesti sovittu
     * 3 = isän luona, oikeuden päätös tai yhteisesti sovittu
     * 4 = vuoroasuminen, asuu vuorotellen vanhempiensa luona, oikeuden päätös tai yhteisesti sovittu
     * 5 = vuoroasuminen äidin ja oheishuoltajan kanssa, oikeuden päätös tai yhteisesti sovittu
     * 6 = vuoroasuminen isän ja oheishuoltajan kanssa, oikeuden päätös tai yhteisesti sovittu
     * 7 = vuoroasuminen vanhempien ja oheishuoltajan kanssa, oikeuden päätös tai yhteisesti sovittu
     * 8 = oheishuoltajan/oheishuoltajien kanssa, oikeuden päätös
     */
    private String asuminen;

    /**
     * Asumispäätökseen liittyvä asumisen alkupäivä, esim. vuoroasuminen.
     */
    @Column(name = "asuminen_alkupvm")
    private LocalDate asuminenAlkupvm;

    /**
     * Asumispäätökseen liittyvä asumisen päättymispäivä, esim. vuoroasuminen.
     */
    @Column(name = "asuminen_loppupvm")
    private LocalDate asuminenLoppupvm;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hetuton_henkilo")
    private HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "huoltaja_oikeudet",
            uniqueConstraints = @UniqueConstraint(name = "huoltaja_oikeudet_oikeudet_id_uk", columnNames = "oikeudet_id"),
            foreignKey = @ForeignKey(name = "huoltaja_oikeudet_huoltaja_id_fk"),
            inverseForeignKey = @ForeignKey(name = "huoltaja_oikeudet_oikeudet_id_fk"))
    private Set<Oikeus> oikeudet = new HashSet<>();

    @Builder
    public Huoltaja(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu, String laji,
                    String rooli, LocalDate startDate, LocalDate endDate,
                    String asuminen, LocalDate asuminenAlkupvm, LocalDate asuminenLoppupvm,
                    HenkilotunnuksetonHenkilo henkilotunnuksetonHenkilo, Set<Oikeus> oikeudet) {
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        this.laji = laji;
        this.rooli = rooli;
        this.startDate = startDate;
        this.endDate = endDate;
        this.asuminen = asuminen;
        this.asuminenAlkupvm = asuminenAlkupvm;
        this.asuminenLoppupvm = asuminenLoppupvm;
        this.henkilotunnuksetonHenkilo = henkilotunnuksetonHenkilo;
        this.oikeudet = oikeudet;
    }

    @Override
    protected Set<Muutostapa> getRedundantChanges() {
        return EnumSet.of(Muutostapa.LISATIETO, Muutostapa.KORJATTAVAA);
    }

    @Override
    public boolean isVoimassa(Context context) {
        return (this.startDate == null || context.getLocalDateNow().isAfter(this.startDate))
                && (this.endDate == null || context.getLocalDateNow().isBefore(this.endDate));
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (Muutostapa.POISTETTU == getMuutostapa()) {
            henkilo.getHuoltajat().removeIf(this::huoltajaMatches);
        } else if (HenkiloUpdateUtil.localdateIsBetween(this.startDate, this.endDate, context.getLocalDateNow())) {
            // Samaan huoltajaan voi olla useita tietoryhmiä samalla rivillä
            HuoltajaCreateDto huoltajaCreateDto = Optional.ofNullable(henkilo.getHuoltajat())
                    .filter(huoltajat -> !huoltajat.isEmpty())
                    .map(huoltajat -> huoltajat.stream().filter(this::huoltajaMatches).findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .orElseGet(HuoltajaCreateDto::new);
            huoltajaCreateDto.setHetu(this.hetu);
            huoltajaCreateDto.setHuoltajuusAlku(this.getStartDate());
            huoltajaCreateDto.setHuoltajuusLoppu(this.getEndDate());
            if (StringUtils.isEmpty(this.hetu)) {
                huoltajaCreateDto.setEtunimet(this.henkilotunnuksetonHenkilo.getFirstNames());
                huoltajaCreateDto.setKutsumanimi(this.henkilotunnuksetonHenkilo.getFirstNames());
                huoltajaCreateDto.setSukunimi(this.henkilotunnuksetonHenkilo.getLastname());
                huoltajaCreateDto.setKansalaisuusKoodi(Collections.singleton(this.henkilotunnuksetonHenkilo.getNationality()));
                huoltajaCreateDto.setSyntymaaika(this.henkilotunnuksetonHenkilo.getDateOfBirth());
                huoltajaCreateDto.setHetu(null);
            }
            henkilo.getHuoltajat().add(huoltajaCreateDto);
        }
    }

    private boolean huoltajaMatches(HuoltajaCreateDto huoltajaCreateDto) {
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
