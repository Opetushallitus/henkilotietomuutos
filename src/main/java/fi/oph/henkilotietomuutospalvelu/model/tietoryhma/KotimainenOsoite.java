package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.KotimainenOsoiteParser;
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
@DiscriminatorValue("kotimainen_osoite")
@Getter
@Setter
@NoArgsConstructor
public class KotimainenOsoite extends YhteystietoTietoryhma<KotimainenOsoite> {

    private static final KotimainenOsoiteParser PARSER = new KotimainenOsoiteParser();

    private String lahiosoite;

    @Column(name = "lahiosoite_sv")
    private String lahiosoiteSV;

    private String katunumero;

    private String porraskirjain;

    private String huonenumero;

    private String jakokirjain;

    private String postinumero;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public KotimainenOsoite(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String lahiosoite, String lahiosoiteSV,
                            String katunumero, String porraskirjain, String huonenumero, String jakokirjain,
                            String postinumero, LocalDate startDate, LocalDate endDate) {
        super(ryhmatunnus, muutostapa);
        this.lahiosoite = lahiosoite;
        this.lahiosoiteSV = lahiosoiteSV;
        this.katunumero = katunumero;
        this.porraskirjain = porraskirjain;
        this.huonenumero = huonenumero;
        this.jakokirjain = jakokirjain;
        this.postinumero = postinumero;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected KoodistoYhteystietoTyyppi getTyyppi() {
        return KoodistoYhteystietoTyyppi.KOTIMAINEN;
    }

    @Override
    protected void updateYhteystietoryhma(Context context, YhteystiedotRyhmaDto yhteystietoryhma) {
        String asiointikieli = HenkiloUtils.getAsiointikieli(context.getCurrentHenkilo());
        String katuosoite = Katuosoite.builder()
                .lahiosoiteFi(lahiosoite)
                .lahiosoiteSv(lahiosoiteSV)
                .katunumero(katunumero)
                .porraskirjain(porraskirjain)
                .huonenumero(huonenumero)
                .jakokirjain(jakokirjain)
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

    @Override
    protected KotimainenOsoite getThis() {
        return this;
    }

    @Override
    protected KotimainenOsoiteParser getParser() {
        return null;
    }
}
