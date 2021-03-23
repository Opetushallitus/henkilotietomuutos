package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.SyntymaKotikuntaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("syntyma_kotikunta")
@NoArgsConstructor
@Getter
public class SyntymaKotikunta extends Tietoryhma<SyntymaKotikunta> {

    private static final SyntymaKotikuntaParser PARSER = new SyntymaKotikuntaParser();

    private String kuntakoodi; // kolmenumeroinen

    @Builder
    public SyntymaKotikunta(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String kuntakoodi) {
        super(ryhmatunnus, muutostapa);
        this.kuntakoodi = kuntakoodi;
    }

    @Override
    protected SyntymaKotikunta getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<SyntymaKotikunta> getParser() {
        return PARSER;
    }
}
