package fi.oph.henkilotietomuutospalvelu.config.scheduling;

import com.google.common.collect.Lists;
import fi.oph.henkilotietomuutospalvelu.config.properties.SchedulingProperties;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class ScheduledTasksTest {
    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @Mock
    private MuutostietoService muutostietoService;

    @Mock
    private SchedulingProperties schedulingProperties;

    @Test
    public void importAllChangesWithNoChanges() throws Exception {
        given(this.muutostietoService.importMuutostiedot(eq(0))).willReturn(Lists.newArrayList());

        ReflectionTestUtils.invokeMethod(scheduledTasks, "importAllChanges");
        verify(muutostietoService, times(1)).importMuutostiedot(eq(0));
    }

    @Test
    public void importAllChanges() throws Exception {
        MuutostietoDto muutostietoDto = MuutostietoDto.builder()
                .rivi(1)
                .build();
        given(this.muutostietoService.importMuutostiedot(eq(0))).willReturn(Lists.newArrayList(muutostietoDto));
        given(this.muutostietoService.importMuutostiedot(eq(1))).willReturn(Lists.newArrayList());

        ReflectionTestUtils.invokeMethod(scheduledTasks, "importAllChanges");
        verify(muutostietoService, times(1)).importMuutostiedot(eq(0));
        verify(muutostietoService, times(1)).importMuutostiedot(eq(1));
    }

}