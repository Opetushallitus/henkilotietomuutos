package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.KotikuntaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("kotikunta")
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class Kotikunta extends Tietoryhma<Kotikunta> {

    private static final KotikuntaParser PARSER = new KotikuntaParser();

    /**
     * Kolmenumeroinen kuntanumero.
     */
    private String code;

    @Column(name = "move_date")
    private LocalDate moveDate;

    @Builder
    public Kotikunta(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, String code, LocalDate moveDate) {
        super(ryhmatunnus, muutostapa);
        this.code = code;
        this.moveDate = moveDate;
    }

    @Override
    protected Kotikunta getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<Kotikunta> getParser() {
        return PARSER;
    }

    @Override
    protected void updateHenkiloInternal(Context context, HenkiloForceUpdateDto henkilo) {
        henkilo.setKotikunta(code);
    }

}
