package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.utils.HenkiloUtils;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Optional;

import static fi.oph.henkilotietomuutospalvelu.utils.YhteystietoUtils.setYhteystietoArvo;

@Entity
@DiscriminatorValue("tilapainen_ulkomainen_osoite")
@NoArgsConstructor
public class TilapainenUlkomainenOsoite extends UlkomainenOsoite {

    public TilapainenUlkomainenOsoite(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String streetAddress, String municipality,
                            String countryCode, LocalDate startDate, LocalDate endDate, String additionalInformation) {
        super(ryhmatunnus, muutostapa, streetAddress, municipality, countryCode, startDate, endDate, additionalInformation);
    }

    public static TilapainenUlkomainenOsoite from(UlkomainenOsoite osoite) {
        return new TilapainenUlkomainenOsoite(
                Ryhmatunnus.ULKOMAINEN_OSOITE_TILAPAINEN, osoite.getMuutostapa(), osoite.getStreetAddress(),
                osoite.getMunicipality(), osoite.getCountryCode(), osoite.getStartDate(), osoite.getEndDate(),
                osoite.getAdditionalInformation()
        );
    }

    @Override
    protected KoodistoYhteystietoTyyppi getTyyppi() {
        return KoodistoYhteystietoTyyppi.TILAPAINEN_ULKOMAINEN;
    }

}
