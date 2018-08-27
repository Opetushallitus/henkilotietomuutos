package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("kutsumanimi")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Kutsumanimi extends Tietoryhma {

    private String name;
    private String type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "non_standard_characters")
    private Boolean nonStandardCharacters;

    @Builder
    public Kutsumanimi(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String name, String type,
                       LocalDate startDate, LocalDate endDate, Boolean nonStandardCharacters){
        super(ryhmatunnus, muutostapa);
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nonStandardCharacters = nonStandardCharacters;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (HenkiloUpdateUtil.localdateExistsAndIsBeforeNow(this.startDate)
                && HenkiloUpdateUtil.localdateNotExistOrIsAfterNow(this.endDate)) {
            // validointi tehdään vasta kun etunimetkin on varmasti asetettu (kts. MuutostietoHandleServiceImpl)
            henkilo.setKutsumanimi(name);
        }
        else {
            log.info("Got outdated kutsumanimi. Not updating.");
        }
    }
}
