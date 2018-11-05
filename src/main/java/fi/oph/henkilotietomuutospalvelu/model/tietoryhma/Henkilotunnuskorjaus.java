package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@Entity
@DiscriminatorValue("henkilotunnuskorjaus")
@Getter
@Setter
@NoArgsConstructor
public class Henkilotunnuskorjaus extends Tietoryhma {

    /**
     * Henkilön nimi tr 004 (nykyinen nimi) annetaan aina lisätietona (muutosattribuutilla 0=lisätieto).
     * Henkilötunnuskorjauksen yhteydessä annetaan kaikki ne kannasta löytyvät henkilötunnukset, jotka henkilöllä
     * on ollut.
     *
     * Esim.
     * 1. Henkilöllä on yksi aktiivi tunnus, joka muutetaan toiseksi.
     * Tunnisteosassa annetaan vanha tunnus ja tietoryhmä 001 kahdesti:
     * ensimmäisessä vanhan tunnuksen passivointi eli muutos, vanha tunnus ja voimassaolo =2, toisessa
     * ryhmässä lisäys, uusi tunnus ja voimassaolo =1.
     *
     * 2. Henkilöllä on kaksi tai useampia aktiivisia tunnuksia, joista ylimääräiset passivoidaan.
     * Tunnisteosassa annetaan voimaan jäävä tunnus ja tietoryhmä 001 tarvittavan monta kertaa:
     * ensimmäisessä lisätieto, voimaan jäävä tunnus ja voimassaolo =1, seuraavissa ryhmissä poistettavan
     * tunnuksen passivointi eli muutos, tunnus ja voimassaolo =2.
     */

    private String hetu;
    private Boolean active;

    @Builder
    public Henkilotunnuskorjaus(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String hetu, Boolean active){
        super(ryhmatunnus, muutostapa);
        this.hetu = hetu;
        this.active = active;
    }

    @Override
    public void updateHenkilo(Context context, HenkiloForceUpdateDto henkilo) {
        // There is a case where we get MUUTETTU + LISATIETO. If the LISATIETO(active=true) we should verify current hetu matches.
        Collection<Muutostapa> allowedMuutostapas = Arrays.asList(Muutostapa.LISATTY, Muutostapa.MUUTETTU, Muutostapa.LISATIETO);
        if (allowedMuutostapas.contains(this.getMuutostapa())) {
            this.updateHenkiloInternal(context, henkilo);
        }
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (this.isActiveAndModifies(context.getCurrentHenkilo().getHetu())) {
                henkilo.setHetu(hetu);
        }

        if (henkilo.getYksiloityHetu() == null) {
            henkilo.setYksiloityHetu(new HashSet<>());
        }
        henkilo.getYksiloityHetu().add(hetu);
    }

    private boolean isActiveAndModifies(String currentHetu) {
        return Boolean.TRUE.equals(this.active) && StringUtils.hasLength(this.hetu) && !this.hetu.equals(currentHetu);
    }
}
