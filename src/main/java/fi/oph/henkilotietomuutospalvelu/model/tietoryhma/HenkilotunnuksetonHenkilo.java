package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Gender;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.HenkilotunnuksetonHenkiloParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("henkilotunnukseton_henkilo")
@Getter
@Setter
@NoArgsConstructor
public class HenkilotunnuksetonHenkilo extends Tietoryhma<HenkilotunnuksetonHenkilo> {

    private static final HenkilotunnuksetonHenkiloParser PARSER = new HenkilotunnuksetonHenkiloParser();

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "first_names")
    private String firstNames;

    /** Kolmenumeroinen kansalaisuuskoodi tai 998 jos kansalaisuus annetaan selkokielisenä. */
    private String nationality;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Builder
    public HenkilotunnuksetonHenkilo(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, LocalDate dateOfBirth, Gender gender,
                                     String lastname, String firstNames, String nationality, String additionalInformation) {
        super(ryhmatunnus, muutostapa);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.lastname = lastname;
        this.firstNames = firstNames;
        this.nationality = nationality;
        this.additionalInformation = additionalInformation;
    }

    @Override
    protected HenkilotunnuksetonHenkilo getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<HenkilotunnuksetonHenkilo> getParser() {
        return PARSER;
    }
}
