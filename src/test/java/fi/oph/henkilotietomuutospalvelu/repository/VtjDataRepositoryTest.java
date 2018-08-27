package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class VtjDataRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private VtjDataRepository vtjDataRepository;

    @Test
    public void onlyUnprocessedVtjDataIsFetched() {
        VtjDataEvent vtjDataEvent1 = VtjDataEvent.builder()
                .vtjdataTimestamp(null)
                .hetu("hetu1")
                .build();
        this.testEntityManager.persist(vtjDataEvent1);

        VtjDataEvent vtjDataEvent2 = VtjDataEvent.builder()
                .vtjdataTimestamp(LocalDateTime.now())
                .hetu("hetu2")
                .build();
        this.testEntityManager.persist(vtjDataEvent2);

        List<VtjDataEvent> vtjDataEvents = this.vtjDataRepository.findByVtjdataTimestampIsNull();
        assertThat(vtjDataEvents)
                .extracting(VtjDataEvent::getVtjdataTimestamp, VtjDataEvent::getHetu)
                .containsExactly(Tuple.tuple(null, "hetu1"));
    }
}