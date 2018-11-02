package fi.oph.henkilotietomuutospalvelu.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.IntegrationTest;
import fi.oph.henkilotietomuutospalvelu.client.KoodistoClient;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Caching is enabled for this test.
 */
@IntegrationTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.cache.type=jcache", "spring.cache.jcache.provider=org.ehcache.jsr107.EhcacheCachingProvider", "spring.cache.cache-names=koodisto"})
public class KoodistoServiceIntegrationTest {
    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private KoodistoClient koodistoClient;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private OphHttpClient ophHttpClient;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setup() {
        cacheManager.getCache("koodisto").clear();

        OphHttpRequest ophHttpRequest = Mockito.mock(OphHttpRequest.class);
        given(this.ophHttpClient.get(any(), any()))
                .willReturn(ophHttpRequest);
        given(ophHttpRequest.expectStatus(any())).willReturn(ophHttpRequest);
        given(ophHttpRequest.accept(any())).willReturn(ophHttpRequest);
        given(ophHttpRequest.retryOnError(any())).willReturn(ophHttpRequest);
        given(ophHttpRequest.execute(any())).willReturn(new KoodiDto[0]);
    }

    @Test
    public void testListCaching() {
        koodistoService.list(Koodisto.KIELI);
        koodistoService.list(Koodisto.KIELI);
        verify(ophHttpClient, times(1)).get(any(), any());
    }

    @Test
    public void testListAsMapCaching() {
        koodistoService.listAsMap(Koodisto.KIELI);
        koodistoService.listAsMap(Koodisto.KIELI);
        verify(ophHttpClient, times(1)).get(any(), any());
    }

    @Test
    public void testIsKoodiValidCaching() {
        koodistoService.isKoodiValid(Koodisto.KIELI, "koodi");
        koodistoService.isKoodiValid(Koodisto.KIELI, "koodi");
        verify(ophHttpClient, times(1)).get(any(), any());
    }

}