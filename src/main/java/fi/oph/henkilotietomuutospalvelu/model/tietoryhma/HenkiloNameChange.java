package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("henkilo_name_change")
@Getter
@NoArgsConstructor
public class HenkiloNameChange extends Tietoryhma {

    private String name;

    @Column(name = "name_type")
    @Enumerated(EnumType.STRING)
    private NameType nameType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "has_non_standard_characters")
    private Boolean hasNonStandardCharacters;

    @Builder
    public HenkiloNameChange(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String name, NameType nameType,
                             LocalDate startDate, LocalDate endDate, Boolean hasNonStandardCharacters){
        super(ryhmatunnus, muutostapa);
        this.name = name;
        this.nameType = nameType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasNonStandardCharacters = hasNonStandardCharacters;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        if (HenkiloUpdateUtil.localdateExistsAndIsBeforeNow(this.startDate) && this.endDate == null) {
            if (this.nameType == NameType.ETUNIMI) {
                henkilo.setEtunimet(this.name);
            }
            if (this.nameType == NameType.SUKUNIMI) {
                henkilo.setSukunimi(this.name);
            }
            if (this.nameType == NameType.KUTSUMANIMI) {
                // validointi tehdään vasta kun etunimetkin on varmasti asetettu (kts. MuutostietoHandleServiceImpl)
                henkilo.setKutsumanimi(name);
            }
        }
    }
}
