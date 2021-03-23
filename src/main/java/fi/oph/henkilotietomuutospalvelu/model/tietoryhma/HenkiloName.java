package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.HenkiloNameParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("henkilo_name")
@Getter
@NoArgsConstructor
public class HenkiloName extends Tietoryhma {

    /**
     * Tietoryhmä toimitetaan lisäyksenä ensimmäistä kertaa henkilön nimitietoja ilmoitettaessa esim. perustietojen
     * poiminnassa tai nimeä lapselle annettaessa (XBL), muutoksena henkilön etu- tai sukunimen muuttuessa ja lisäksi
     * se välitetään lisätietona henkilötunnuksiin liittyvissä tapahtumissa (tietoryhmän 001 yhteydessä).
     */

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_names")
    private String firstNames;

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    /**
     * tyhjä=nimessä ei standardiin kuulumattomia merkkejä
     * 1=standardiin kuulumaton merkki sukunimessä
     * 2=standardiin kuulumaton merkki etunimessä
     * 3=standardiin kuulumaton merkki sekä suku- että etunimessä
     */
    @Column(name = "additional_information")
    private String additionalInformation;

    @Builder
    public HenkiloName(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String lastName, String firstNames,
                       LocalDate lastUpdateDate, String additionalInformation){
        super(ryhmatunnus, muutostapa);
        this.lastName = lastName;
        this.firstNames = firstNames;
        this.lastUpdateDate = lastUpdateDate;
        this.additionalInformation = additionalInformation;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        henkilo.setEtunimet(this.firstNames);
        henkilo.setSukunimi(this.lastName);
    }
}
