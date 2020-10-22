package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("tilapainen_kotimainen_osoite")
@NoArgsConstructor
public class TilapainenKotimainenOsoite extends KotimainenOsoite {

    public TilapainenKotimainenOsoite(Muutostapa muutostapa, String lahiosoite, String lahiosoiteSV,
                                      String katunumero, String porraskirjain, String huonenumero, String jakokirjain,
                                      String postinumero, LocalDate startDate, LocalDate endDate) {
        super(
                Ryhmatunnus.KOTIMAINEN_OSOITE_TILAPAINEN, muutostapa, lahiosoite, lahiosoiteSV, katunumero,
                porraskirjain, huonenumero, jakokirjain, postinumero, startDate, endDate);
    }

    public static TilapainenKotimainenOsoite from(KotimainenOsoite osoite) {
        return new TilapainenKotimainenOsoite(
                osoite.getMuutostapa(), osoite.getLahiosoite(),  osoite.getLahiosoiteSV(), osoite.getKatunumero(),
                osoite.getPorraskirjain(), osoite.getHuonenumero(),  osoite.getJakokirjain(), osoite.getPostinumero(),
                osoite.getStartDate(), osoite.getEndDate()
        );
    }

    @Override
    protected KoodistoYhteystietoTyyppi getTyyppi() {
        return KoodistoYhteystietoTyyppi.TILAPAINEN_KOTIMAINEN;
    }

}
