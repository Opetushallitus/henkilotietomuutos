package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import com.google.common.collect.Sets;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KansalaisuusDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("kansalaisuus")
@Getter
@Slf4j
@NoArgsConstructor
public class Kansalaisuus extends Tietoryhma {

    /**
     * Kansalaisuudet valtiokoodiston (ISO3166-standardi) mukaisina tai sitten selväkielisenä tietona.
     * Selväkielisen tiedon sisältävä tietoryhmä seuraa, jos koodi on 998.
     * Kansalaisuustietoryhmien lajittelujärjestys: muutosattribuutti, saamispäivä.
     */

    private String code;
    private Boolean valid;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public Kansalaisuus(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String code, Boolean valid,
                        LocalDate startDate, LocalDate endDate){
        super(ryhmatunnus, muutostapa);
        this.code = code;
        this.valid = valid;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        // We don't schedule upcoming updates so startDate is ignored
        if (this.valid && (this.endDate == null || LocalDate.now().isBefore(this.endDate))) {
            KansalaisuusDto newKansalaisuus = new KansalaisuusDto();
            newKansalaisuus.setKansalaisuusKoodi(this.code);

            // New info so overriding any old ones
            if (this.getMuutostapa().equals(Muutostapa.LISATTY)) {
                if (CollectionUtils.isEmpty(henkilo.getKansalaisuus())) {
                    henkilo.setKansalaisuus(Sets.newHashSet(newKansalaisuus));
                }
                // Kaksoiskansalaisuus (in other)
                else {
                    Set<KansalaisuusDto> multipleNewKansalaisuusSet = Sets.newHashSet(henkilo.getKansalaisuus());
                    multipleNewKansalaisuusSet.add(newKansalaisuus);
                    henkilo.setKansalaisuus(multipleNewKansalaisuusSet);
                }
            }
            // Fix or change so add if not already in kansalaisuus list
            else {
                HenkiloDto currentHenkilo = context.getCurrentHenkilo();
                if (currentHenkilo.getKansalaisuus().stream()
                        .map(KansalaisuusDto::getKansalaisuusKoodi)
                        .noneMatch(kansalaisuusKoodi -> kansalaisuusKoodi.equals(this.code))) {
                    if (CollectionUtils.isEmpty(henkilo.getKansalaisuus())) {
                        Set<KansalaisuusDto> kansalaisuusDtos = new HashSet<>(currentHenkilo.getKansalaisuus());
                        kansalaisuusDtos.add(newKansalaisuus);
                        henkilo.setKansalaisuus(kansalaisuusDtos);
                    }
                    // Something already written in UpdateDto (has multiple kansalaisuus)
                    else {
                        Set<KansalaisuusDto> multipleNewKansalaisuusSet = Sets.newHashSet(henkilo.getKansalaisuus());
                        multipleNewKansalaisuusSet.add(newKansalaisuus);
                        henkilo.setKansalaisuus(multipleNewKansalaisuusSet);
                    }
                }
                else {
                    log.warn(String.format("Kansalaisuuskoodi %s already exist in henkilo %s data", this.code, currentHenkilo.getOidHenkilo()));
                }
            }
        }
        else {
            log.warn(String.format("Received invalid or outdated kansalaisuus for henkio %s", henkilo.getOidHenkilo()));
        }
    }
}
