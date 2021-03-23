package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.UlkomainenSyntymapaikkaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ulkomainen_syntymapaikka")
@Getter
@Setter
@NoArgsConstructor
public class UlkomainenSyntymapaikka extends Tietoryhma<UlkomainenSyntymapaikka> {

    private static final UlkomainenSyntymapaikkaParser PARSER = new UlkomainenSyntymapaikkaParser();

    /** Valtiokoodi ISO3166 muodossa. Jos koodi on 998, annetaan tarkentava kuvaus selväkielisena tietona. */
    @Column(name = "country_code")
    private String countryCode;

    private String location;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Builder
    public UlkomainenSyntymapaikka(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String countryCode,
                                   String location, String additionalInformation) {
        super(ryhmatunnus, muutostapa);
        this.countryCode = countryCode;
        this.location = location;
        this.additionalInformation = additionalInformation;
    }

    @Override
    protected UlkomainenSyntymapaikka getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<UlkomainenSyntymapaikka> getParser() {
        return PARSER;
    }
}
