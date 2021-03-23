package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.PostiosoiteParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.oph.henkilotietomuutospalvelu.utils.HenkiloUtils;
import fi.oph.henkilotietomuutospalvelu.utils.Katuosoite;
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
@DiscriminatorValue("postiosoite")
@Getter
@Setter
@NoArgsConstructor
public class Postiosoite extends YhteystietoTietoryhma {

    private String postiosoite;

    @Column(name = "postiosoite_sv")
    private String postiosoiteSv;

    private String postinumero;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public Postiosoite(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa,
                       String postiosoite, String postiosoiteSv, String postinumero,
                       LocalDate startDate, LocalDate endDate){
        super(ryhmatunnus, muutostapa);
        this.postiosoite = postiosoite;
        this.postiosoiteSv = postiosoiteSv;
        this.postinumero = postinumero;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected KoodistoYhteystietoTyyppi getTyyppi() {
        return KoodistoYhteystietoTyyppi.KOTIMAINEN_POSTIOSOITE;
    }

    @Override
    protected void updateYhteystietoryhma(Context context, YhteystiedotRyhmaDto yhteystietoryhma) {
        String asiointikieli = HenkiloUtils.getAsiointikieli(context.getCurrentHenkilo());
        String katuosoite = Katuosoite.builder()
                .lahiosoiteFi(postiosoite)
                .lahiosoiteSv(postiosoiteSv)
                .build()
                .getAsString(asiointikieli);
        setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE, katuosoite);
        setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_POSTINUMERO, postinumero);
        Optional.ofNullable(postinumero)
                .filter(StringUtils::hasLength)
                .flatMap(postinumero -> context.getPostitoimipaikka(postinumero, asiointikieli))
                .ifPresent(postitoimipaikka -> setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_KAUPUNKI, postitoimipaikka));
        context.getMaa(ISO3166_FI, asiointikieli)
                .ifPresent(maa -> setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_MAA, maa));
    }
}
