package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.IntegrationTest;
import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import fi.oph.henkilotietomuutospalvelu.model.type.VtjEventType;
import fi.oph.henkilotietomuutospalvelu.repository.VtjDataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(SpringRunner.class)
@IntegrationTest
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = {"DELETE vtj_data_event;"})
})
public class HetuServiceTest {

    @Autowired
    private HetuService hetuService;
    @Autowired
    private VtjDataRepository vtjDataRepository;
    @MockBean
    private FileService fileService;

    @Test
    public void updateHetusToDb() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        HetuDto hetuDto1 = new HetuDto();
        hetuDto1.setAddedHetus(asList("hetu1", "hetu2", "hetu3"));

        hetuService.updateHetusToDb(hetuDto1);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(
                        tuple("hetu1", VtjEventType.ADD), tuple("hetu2", VtjEventType.ADD),
                        tuple("hetu3", VtjEventType.ADD));
        assertThat(hetuService.updateHetusToVtj()).containsExactlyInAnyOrder("hetu1", "hetu2", "hetu3");

        hetuService.updateHetusToDb(hetuDto1);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(
                        tuple("hetu1", VtjEventType.ADD), tuple("hetu2", VtjEventType.ADD),
                        tuple("hetu3", VtjEventType.ADD));
        assertThat(hetuService.updateHetusToVtj()).isEmpty();

        HetuDto hetuDto2 = new HetuDto();
        hetuDto2.setRemovedHetus(asList("hetu2", "hetu4"));

        hetuService.updateHetusToDb(hetuDto2);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(
                        tuple("hetu1", VtjEventType.ADD), tuple("hetu2", VtjEventType.ADD),
                        tuple("hetu3", VtjEventType.ADD), tuple("hetu2", VtjEventType.REMOVE),
                        tuple("hetu4", VtjEventType.REMOVE));
        assertThat(hetuService.updateHetusToVtj()).containsExactlyInAnyOrder("hetu2", "hetu4");

        hetuService.updateHetusToDb(hetuDto2);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(
                        tuple("hetu1", VtjEventType.ADD), tuple("hetu2", VtjEventType.ADD),
                        tuple("hetu3", VtjEventType.ADD), tuple("hetu2", VtjEventType.REMOVE),
                        tuple("hetu4", VtjEventType.REMOVE));
        assertThat(hetuService.updateHetusToVtj()).isEmpty();
    }

    @Test
    public void updateHetusToDbNullsFirst() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        HetuDto hetuDto1 = new HetuDto();
        hetuDto1.setAddedHetus(asList("hetu1"));

        hetuService.updateHetusToDb(hetuDto1);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(tuple("hetu1", VtjEventType.ADD));
        assertThat(hetuService.updateHetusToVtj()).containsExactlyInAnyOrder("hetu1");

        HetuDto hetuDto2 = new HetuDto();
        hetuDto2.setRemovedHetus(asList("hetu1"));

        hetuService.updateHetusToDb(hetuDto2);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(tuple("hetu1", VtjEventType.ADD), tuple("hetu1", VtjEventType.REMOVE));

        hetuService.updateHetusToDb(hetuDto2);

        assertThat(vtjDataRepository.findAll())
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(tuple("hetu1", VtjEventType.ADD), tuple("hetu1", VtjEventType.REMOVE));
    }

}
