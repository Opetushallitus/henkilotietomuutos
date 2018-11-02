package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KielisyysDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("aidinkieli")
@Getter
@Setter
@NoArgsConstructor
public class Aidinkieli extends Tietoryhma {

    /**
     * Äidinkieli on esitetty ISO639-standardin mukaisella kielikoodilla.
     * Poikkeustapauksissa kielikoodiksi on annettu "98" ja lisätiedoissa lukee selvennys.
     */

    @Column(name = "language_code")
    private String languageCode; // ISO639-standardin mukainen kielikoodi tai "98"
    @Column(name = "additional_information")
    private String additionalInformation; // Kielikoodin "98" selvennys (esim. suomalainen viittomakieli)

    @Builder
    public Aidinkieli(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String languageCode, String additionalInformation){
        super(ryhmatunnus, muutostapa);
        this.languageCode = languageCode;
        this.additionalInformation = additionalInformation;
    }


    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        KielisyysDto aidinkieli = new KielisyysDto();
        aidinkieli.setKieliKoodi(this.languageCode);
        henkilo.setAidinkieli(aidinkieli);
    }
}
