package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.service.parse.EdunvalvontaValtuutusParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("edunvalvonta_valtuutus")
@NoArgsConstructor
@Getter
public class EdunvalvontaValtuutus extends Tietoryhma<EdunvalvontaValtuutus> {

    private static final EdunvalvontaValtuutusParser PARSER = new EdunvalvontaValtuutusParser();

    /**
     * Edunvalvontavaltuutuksen tietoryhmässä kerrotaan edunvalvontavaltuutuslain mukaisen edunvalvontavaltuutuksen
     * alkamis- ja päättymispäivä, sekä edunvalvojavaltuutettujen tietoryhmien lukumäärä. Edunvalvontavaltuutustietoryhmää
     * seuraa siten aina ko. luvun kertoma määrä edunvalvojavaltuutettutietoryhmiä. Edunvalvontavaltuutustiedot
     * välitetään ainoastaan henkilölle, joka on määritellyt valtuutettunsa.
     */

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** Määräys tehtävien jaosta edunvalvontavaltuutettujen kesken on annettu. */
    @Column(name = "duties_started")
    private Boolean dutiesStarted; // hmm, tämän tulisi kai olla "shared" eikä "started"?!

    @Column(name = "edunvalvoja_valtuutetut")
    private Long edunvalvojaValtuutetut;

    @Builder
    public EdunvalvontaValtuutus(Ryhmatunnus ryhmatunnus, Muutostapa muutostapa, LocalDate startDate, LocalDate endDate,
                                 Boolean dutiesStarted, Long edunvalvojaValtuutetut) {
        super(ryhmatunnus, muutostapa);
        this.startDate = startDate;
        this.endDate = endDate;
        this.dutiesStarted = dutiesStarted;
        this.edunvalvojaValtuutetut = edunvalvojaValtuutetut;
    }

    @Override
    protected EdunvalvontaValtuutus getThis() {
        return this;
    }

    @Override
    protected TietoryhmaParser<EdunvalvontaValtuutus> getParser() {
        return PARSER;
    }
}
