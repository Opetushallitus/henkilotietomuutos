package fi.oph.henkilotietomuutospalvelu.service.impl;

import com.google.common.collect.Lists;
import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import fi.oph.henkilotietomuutospalvelu.model.type.VtjEventType;
import fi.oph.henkilotietomuutospalvelu.repository.VtjDataRepository;
import fi.oph.henkilotietomuutospalvelu.service.FileService;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class HetuServiceImplTest {
    @InjectMocks
    private HetuServiceImpl hetuService;

    @Mock
    private FileService fileService;

    @Mock
    private VtjDataRepository vtjDataRepository;

    @Captor
    private ArgumentCaptor<List<VtjDataEvent>> listArgumentCaptor;

    @Test
    public void allUpdatedHetusAreSaved() {
        HetuDto hetuDto = new HetuDto();
        List<String> addHetus = Lists.newArrayList("hetu1", "hetu2");
        List<String> removeHetus = Lists.newArrayList("hetu3");
        hetuDto.setAddedHetus(addHetus);
        hetuDto.setRemovedHetus(removeHetus);

        this.hetuService.updateHetusToDb(hetuDto);

        verify(this.vtjDataRepository).saveAll(this.listArgumentCaptor.capture());
        List<VtjDataEvent> savedData = this.listArgumentCaptor.getValue();
        assertThat(savedData)
                .extracting(VtjDataEvent::getHetu, VtjDataEvent::getType)
                .containsExactlyInAnyOrder(Tuple.tuple("hetu1", VtjEventType.ADD),
                        Tuple.tuple("hetu2", VtjEventType.ADD),
                        Tuple.tuple("hetu3", VtjEventType.REMOVE));
        assertThat(savedData)
                .extracting(VtjDataEvent::getVtjdataTimestamp)
                .containsNull();
    }

    @Test
    public void updateHetusToVtj() {
        List<VtjDataEvent> vtjDataEvents = Lists.newArrayList(VtjDataEvent.builder()
                .hetu("hetu1")
                .vtjdataTimestamp(null)
                .type(VtjEventType.ADD)
                .build());
        given(this.vtjDataRepository.findByVtjdataTimestampIsNull()).willReturn(vtjDataEvents);

        Set<String> stringSet = this.hetuService.updateHetusToVtj();

        assertThat(stringSet).containsExactly("hetu1");
        assertThat(vtjDataEvents)
                .extracting(VtjDataEvent::getVtjdataTimestamp)
                .doesNotContainNull();
    }

}