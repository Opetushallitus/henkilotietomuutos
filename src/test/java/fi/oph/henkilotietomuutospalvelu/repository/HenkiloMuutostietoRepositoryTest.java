package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class HenkiloMuutostietoRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private HenkiloMuutostietoRepository henkiloMuutostietoRepository;

    @Test
    public void lastRowCountIsReturnedCorrectly() {
        Tiedosto tiedosto = this.testEntityManager.persist(new Tiedosto("File1", 1));
        HenkiloMuutostietoRivi henkiloMuutostietoRivi1 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi1.setRivi(1);
        henkiloMuutostietoRivi1.setTiedosto(tiedosto);
        this.testEntityManager.persist(henkiloMuutostietoRivi1);

        HenkiloMuutostietoRivi henkiloMuutostietoRivi2 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi2.setRivi(2);
        henkiloMuutostietoRivi2.setTiedosto(tiedosto);
        this.testEntityManager.persist(henkiloMuutostietoRivi2);

        Tiedosto tiedosto2 = this.testEntityManager.persist(new Tiedosto("File2", 1));
        HenkiloMuutostietoRivi henkiloMuutostietoRivi3 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi3.setRivi(1);
        henkiloMuutostietoRivi3.setTiedosto(tiedosto2);
        this.testEntityManager.persist(henkiloMuutostietoRivi3);

        HenkiloMuutostietoRivi henkiloMuutostietoRivi4 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi4.setRivi(1);
        henkiloMuutostietoRivi4.setTiedosto(tiedosto2);
        this.testEntityManager.persist(henkiloMuutostietoRivi4);


        Optional<Integer> lastRow = this.henkiloMuutostietoRepository.findLastRowByTiedostoNimi("File1");

        assertThat(lastRow).contains(2);
    }

    @Test
    public void lastRowWhenFileIsNotFound() {
        Optional<Integer> lastRow = this.henkiloMuutostietoRepository.findLastRowByTiedostoNimi("File1");
        assertThat(lastRow).isEmpty();
    }

    @Test
    public void findHenkiloMuutostietoRiviByQueryHetu() {
        Henkilotunnuskorjaus henkilotunnuskorjaus = new Henkilotunnuskorjaus(Ryhmatunnus.HENKILOTUNNUS_KORJAUS, Muutostapa.LISATTY, "hetu2", true);
        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkilotunnuskorjaus.setHenkiloMuutostietoRivi(henkiloMuutostietoRivi);
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.setTietoryhmaList(Collections.singletonList(henkilotunnuskorjaus));
        this.testEntityManager.persist(henkiloMuutostietoRivi);
        this.testEntityManager.persist(henkilotunnuskorjaus);

        List<HenkiloMuutostietoRivi> rivis = this.henkiloMuutostietoRepository.findHenkiloMuutostietoRiviByQueryHetu("hetu1");
        assertThat(rivis)
                .extracting(HenkiloMuutostietoRivi::getQueryHetu)
                .containsExactly("hetu1");
        assertThat(rivis)
                .hasSize(1)
                .flatExtracting(HenkiloMuutostietoRivi::getTietoryhmaList)
                .hasSize(1)
                .extracting(tietoryhma -> ((Henkilotunnuskorjaus)tietoryhma).getHetu())
                .containsExactly("hetu2");
    }

    @Test
    public void unprocessedRivisByFileNameShouldBeFoundCorrectly() {
        Tiedosto tiedosto1 = new Tiedosto();
        tiedosto1.setFileName("File1");
        tiedosto1.setPartCount(1);
        this.testEntityManager.persist(tiedosto1);

        HenkiloMuutostietoRivi henkiloMuutostietoRivi1 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi1.setTiedosto(tiedosto1);
        henkiloMuutostietoRivi1.setRivi(1);
        henkiloMuutostietoRivi1.setProcessTimestamp(LocalDateTime.now());
        this.testEntityManager.persist(henkiloMuutostietoRivi1);

        HenkiloMuutostietoRivi henkiloMuutostietoRivi2 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi2.setTiedosto(tiedosto1);
        henkiloMuutostietoRivi2.setRivi(2);
        Long id1 = this.testEntityManager.persist(henkiloMuutostietoRivi2).getId();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi3 = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi3.setTiedosto(tiedosto1);
        henkiloMuutostietoRivi3.setRivi(3);
        Long id2 = this.testEntityManager.persist(henkiloMuutostietoRivi3).getId();

        // Just some extra data
        Tiedosto tiedosto2 = new Tiedosto();
        tiedosto2.setFileName("File2");
        tiedosto2.setPartCount(1);
        this.testEntityManager.persist(tiedosto2);

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(tiedosto2);
        henkiloMuutostietoRivi.setRivi(1);
        henkiloMuutostietoRivi.setProcessTimestamp(LocalDateTime.now());
        this.testEntityManager.persist(henkiloMuutostietoRivi);

        List<Long> rivis = this.henkiloMuutostietoRepository
                .findByTiedostoFileNameAndProcessTimestampIsNullOrderByRivi("File1");

        assertThat(rivis)
                .containsExactly(id1, id2);
    }
}
