package fi.oph.henkilotietomuutospalvelu.aspects;


import fi.oph.henkilotietomuutospalvelu.IntegrationTest;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.VtjDataRepository;
import fi.oph.henkilotietomuutospalvelu.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Aspects seem to work only with real beans so to throw the exception we need to do it inside the first call to mocked
 * bean in the real method which tends to be error prone for changes. Also this needs to be real integration test for aspects
 * to load properly.
 */
@IntegrationTest
@RunWith(SpringRunner.class)
public class ExternalNotificationAspectIntegrationTest {
    @Autowired
    private HetuService hetuService;

    @Autowired
    private MuutostietoService muutostietoService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private ViestintaProperties viestintaProperties;

    @MockBean
    private FileService fileService;

    @MockBean
    private VtjDataRepository vtjDataRepository;

    @MockBean
    private FtpProperties ftpProperties;

    @MockBean
    private HenkiloMuutostietoRepository henkiloMuutostietoRepository;

    @Before
    public void setup() {
        given(this.viestintaProperties.getDefaultReceiverEmail()).willReturn("email");
        given(this.viestintaProperties.getFlowToken()).willReturn("token");
    }

    @Test
    public void updateHetusToVtjThrowIsNotified() {
        willThrow(new RuntimeException()).given(this.vtjDataRepository).findByVtjdataTimestampIsNull();
        assertThatThrownBy(() -> this.hetuService.updateHetusToVtj()).isInstanceOf(RuntimeException.class);
        verify(notificationService, times(1)).sendEmailNotification(any(), any());
        verify(notificationService, times(1)).sendFlowdocNotification(any(), any(), any());
    }

    @Test
    public void maxNotificationIntervalShouldBeRespected() {
        given(this.viestintaProperties.getMaxNotificationIntervalInMinutes()).willReturn(60);
        willThrow(new RuntimeException()).given(this.vtjDataRepository).findByVtjdataTimestampIsNull();
        assertThatThrownBy(() -> this.hetuService.updateHetusToVtj()).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> this.hetuService.updateHetusToVtj()).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> this.hetuService.updateHetusToVtj()).isInstanceOf(RuntimeException.class);
        verify(notificationService, times(1)).sendEmailNotification(any(), any());
        verify(notificationService, times(1)).sendFlowdocNotification(any(), any(), any());
    }

    @Test
    public void updateMuutostietosThrowIsNotified() {
        willThrow(new RuntimeException()).given(this.henkiloMuutostietoRepository).findDistinctUnprocessedTiedostoFileName();
        assertThatThrownBy(() -> this.muutostietoService.updateMuutostietos()).isInstanceOf(RuntimeException.class);
        verify(notificationService, times(1)).sendEmailNotification(any(), any());
        verify(notificationService, times(1)).sendFlowdocNotification(any(), any(), any());
    }

    @Test
    public void importMuutostiedotThrowIsNotified() throws Exception {
        given(this.fileService.findNextFile()).willThrow(new RuntimeException());
        assertThatThrownBy(() -> this.muutostietoService.importMuutostiedot(1)).isInstanceOf(RuntimeException.class);
        verify(notificationService, times(1)).sendEmailNotification(any(), any());
        verify(notificationService, times(1)).sendFlowdocNotification(any(), any(), any());
    }

    @Test
    public void downloadFilesThrowIsNotified() throws Exception {
        given(this.muutostietoService.downloadFiles()).willThrow(new RuntimeException());
        assertThatThrownBy(() -> this.muutostietoService.downloadFiles()).isInstanceOf(RuntimeException.class);
        verify(notificationService, times(1)).sendEmailNotification(any(), any());
        verify(notificationService, times(1)).sendFlowdocNotification(any(), any(), any());
    }
}
