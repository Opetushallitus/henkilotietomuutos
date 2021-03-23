package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.AmmattiParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ammatti")
@Getter
@NoArgsConstructor
public class Ammatti extends Tietoryhma<Ammatti> {

    private static final AmmattiParser PARSER = new AmmattiParser();
    private String code;
    private String description;

    @Builder
    public Ammatti(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String code, String description) {
        super(ryhmatunnus, muutostapa);
        this.code = code;
        this.description = description;
    }

    @Override
    protected Ammatti getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<Ammatti> getParser() {
        return PARSER;
    }
}
