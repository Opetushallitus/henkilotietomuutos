package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenOsoiteParser;
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
@DiscriminatorValue("ulkomainen_osoite")
@Getter
@Setter
@NoArgsConstructor
public class UlkomainenOsoite extends YhteystietoTietoryhma<UlkomainenOsoite> {

    private static final UlkomainenOsoiteParser PARSER = new UlkomainenOsoiteParser();

    @Column(name = "street_address")
    private String streetAddress;

    private String municipality;

    /** Valtiokoodi ISO3166 muodossa. Jos koodi on 998, annetaan tarkentava kuvaus selväkielisena tietona. */
    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public UlkomainenOsoite(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String streetAddress, String municipality,
                            String countryCode, LocalDate startDate, LocalDate endDate, String additionalInformation) {
        super(ryhmatunnus, muutostapa);
        this.streetAddress = streetAddress;
        this.municipality = municipality;
        this.countryCode = countryCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.additionalInformation = additionalInformation;
    }

    @Override
    protected KoodistoYhteystietoTyyppi getTyyppi() {
        return KoodistoYhteystietoTyyppi.ULKOMAINEN;
    }

    @Override
    protected void updateYhteystietoryhma(Context context, YhteystiedotRyhmaDto yhteystietoryhma) {
        String asiointikieli = HenkiloUtils.getAsiointikieli(context.getCurrentHenkilo());
        setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE, streetAddress);
        setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_KUNTA, municipality);
        Optional.ofNullable(countryCode)
                .filter(StringUtils::hasLength)
                .flatMap(maakoodi -> context.getMaa(maakoodi, asiointikieli))
                .ifPresent(maa -> setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_MAA, maa));
    }

    @Override
    protected UlkomainenOsoite getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<UlkomainenOsoite> getParser() {
        return PARSER;
    }
}
