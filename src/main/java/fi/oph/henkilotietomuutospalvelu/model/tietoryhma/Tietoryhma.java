package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.IdentifiableAndVersionedEntity;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tietoryhma_type", discriminatorType=DiscriminatorType.STRING)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public abstract class Tietoryhma extends IdentifiableAndVersionedEntity {

    @Transient
    private Ryhmatunnus ryhmatunnus;

    @Column(name = "muutostapa")
    @Enumerated(EnumType.STRING)
    private Muutostapa muutostapa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muutostieto_rivi_id")
    private HenkiloMuutostietoRivi henkiloMuutostietoRivi;

    private static final EnumSet<Muutostapa> REDUNDANT_CHANGES =
            EnumSet.of(Muutostapa.LISATIETO, Muutostapa.POISTETTU, Muutostapa.KORJATTAVAA);

    public Tietoryhma(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa) {
        this.ryhmatunnus = ryhmatunnus;
        this.muutostapa = muutostapa;
    }

    public boolean isVoimassa() {
        return true;
    }

    public void updateHenkilo(Context context, HenkiloForceUpdateDto henkilo) {
        if (!getRedundantChanges().contains(this.getMuutostapa())) {
            updateHenkiloInternal(context, henkilo);
        }
        else {
            log.debug("Redundant change skipped");
        }
    }

    protected Set<Muutostapa> getRedundantChanges() {
        return REDUNDANT_CHANGES;
    }

    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
//        log.warn("Tietoryhma {} is not implemented!", this.getClass());
    }

    public interface Context {
        HenkiloForceReadDto getCurrentHenkilo();
        Optional<String> getPostitoimipaikka(String postinumero, String kieli);
        Optional<String> getMaa(String maakoodi, String kieli);
        LocalDate getLocalDateNow();
    }

}
