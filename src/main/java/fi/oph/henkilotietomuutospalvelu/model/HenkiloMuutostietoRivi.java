package fi.oph.henkilotietomuutospalvelu.model;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Table(name = "henkilo_muutostieto_rivi", schema = "public")
@Getter
@Setter
public class HenkiloMuutostietoRivi extends IdentifiableAndVersionedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiedosto_id")
    private Tiedosto tiedosto;

    @Column(name = "rivi")
    private int rivi;

    // Hetu that is found in oppijanumerorekisteri (will be updated to all rows if hetu changes)
    @Column(name = "query_hetu")
    private String queryHetu;

    @OrderBy
    @OneToMany(mappedBy = "henkiloMuutostietoRivi", cascade = CascadeType.ALL)
    private List<Tietoryhma> tietoryhmaList = new ArrayList<>();

    @Column(name = "process_timestamp")
    private LocalDateTime processTimestamp;

    public void addTietoryhma(Tietoryhma... tietoryhmat) {
        for (Tietoryhma tietoryhma : tietoryhmat) {
            tietoryhma.setHenkiloMuutostietoRivi(this);
            tietoryhmaList.add(tietoryhma);
        }
    }

    public Stream<Tietoryhma> getTietoryhmaStream() {
        return tietoryhmaList.stream();
    }

}
