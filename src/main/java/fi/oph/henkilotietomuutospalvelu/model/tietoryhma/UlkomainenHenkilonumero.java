package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
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
@DiscriminatorValue("ulkomainen_henkilonumero")
@NoArgsConstructor
@Getter
public class UlkomainenHenkilonumero extends Tietoryhma {

    @Column(name = "ulkomainen_henkilonumero_id")
    private String ulkomainenHenkilonumeroId;

    private Gender gender;

    @Column(name = "country_code")
    private String countryCode; // kolminumeroinen

    /**
     * 1=ulkomainen rekisteriviranomainen
     * 2=henkilö itse (ylläpitotapahtumat)
     * 3=tunnistusajo
     */
    private String tietolahde;

    private String type;

    private Boolean valid;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "passivointi_date")
    private LocalDate passivointiDate;

    @Column(name = "save_date_vtj")
    private LocalDate saveDateVTJ;

    @Column(name = "passivointi_date_vtj")
    private LocalDate passivointiDateVTJ;

    /**
     * VTJ:n voimassaoleva, parhaimmaksi oletettu ulkomainen henkilönumero.
     * true = voimassaoleva paras tunnus (vain yhdella ko. valtion tunnuksella kerrallaan)
     * false = muut
     */
    @Column(name = "valid_vtj")
    private Boolean validVTJ;

    @Builder
    public UlkomainenHenkilonumero(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String ulkomainenHenkilonumeroId, Gender gender,
                                   String countryCode, String tietolahde, String type, Boolean valid,
                                   LocalDate issueDate, LocalDate passivointiDate, LocalDate saveDateVTJ,
                                   LocalDate passivointiDateVTJ, Boolean validVTJ) {
        super(ryhmatunnus, muutostapa);
        this.ulkomainenHenkilonumeroId = ulkomainenHenkilonumeroId;
        this.gender = gender;
        this.countryCode = countryCode;
        this.tietolahde = tietolahde;
        this.type = type;
        this.valid = valid;
        this.issueDate = issueDate;
        this.passivointiDate = passivointiDate;
        this.saveDateVTJ = saveDateVTJ;
        this.passivointiDateVTJ = passivointiDateVTJ;
        this.validVTJ = validVTJ;
    }

}
