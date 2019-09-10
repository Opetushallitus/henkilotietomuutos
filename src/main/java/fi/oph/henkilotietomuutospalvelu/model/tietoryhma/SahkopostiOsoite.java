package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

import static fi.oph.henkilotietomuutospalvelu.utils.YhteystietoUtils.setYhteystietoArvo;

@Entity
@DiscriminatorValue("sahkoposti_osoite")
@Getter
@Slf4j
@NoArgsConstructor
public class SahkopostiOsoite extends YhteystietoTietoryhma {


    /**
     * 99=henkilon ilmoittama sahkopostiosoite,
     * 11=maistraatin sahkopostiosoite,
     * 12=vaaliviranomaisen sahkopostiosoite.
     */
    private String lajikoodi;

    private String email;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public SahkopostiOsoite(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String lajikoodi,
                            String email, LocalDate startDate, LocalDate endDate) {
        super(ryhmatunnus, muutostapa);
        this.lajikoodi = lajikoodi;
        this.email = email;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected KoodistoYhteystietoTyyppi getTyyppi() {
        return KoodistoYhteystietoTyyppi.SAHKOINEN_OSOITE;
    }

    @Override
    protected void updateYhteystietoryhma(Context context, YhteystiedotRyhmaDto yhteystietoryhma) {
        setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_SAHKOPOSTI, email);
    }

}
