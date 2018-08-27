package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import fi.oph.henkilotietomuutospalvelu.config.UrlConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.properties.CasProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.OppijanumerorekisteriProperties;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpResponse;
import fi.vm.sade.javautils.http.OphHttpResponseImpl;
import fi.vm.sade.javautils.http.exceptions.UnhandledHttpStatusCodeException;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KansalaisuusDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KielisyysDto;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

import static fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Aidinkieli.KIELIKOODI_TUNTEMATON;
import static fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kansalaisuus.KANSALAISUUSKOODI_TUNTEMATON;
import static fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kotikunta.KUNTAKOODI_TUNTEMATON;
import static fi.vm.sade.javautils.httpclient.OphHttpClient.Header.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ObjectMapper.class)
public class OnrServiceClientImplTest {
    @InjectMocks
    private OnrServiceClientImpl onrServiceClient;

    @Mock
    private UrlConfiguration urlConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CasProperties casProperties;

    @Mock
    private OppijanumerorekisteriProperties oppijanumerorekisteriProperties;

    @Mock
    private OphHttpClient ophHttpClient;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(this.onrServiceClient, "ophHttpClient", this.ophHttpClient);
        ReflectionTestUtils.setField(this.onrServiceClient, "objectMapper", this.objectMapper);
    }

    @Test
    public void foundHenkiloIsReturned() {
        CloseableHttpResponse httpResponse = this.mockResponse("{\"hetu\":\"hetu1\"}", SC_OK, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any())).willReturn(new OphHttpResponseImpl<>(httpResponse));
        Optional<HenkiloDto> optionalHenkiloDto = this.onrServiceClient.getHenkiloByHetu("hetu1");
        assertThat(optionalHenkiloDto)
                .map(HenkiloDto::getHetu)
                .contains("hetu1");
    }

    @Test
    public void henkiloNotFound() {
        CloseableHttpResponse httpResponse = this.mockResponse("", SC_NOT_FOUND, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any())).willReturn(new OphHttpResponseImpl<>(httpResponse));
        Optional<HenkiloDto> optionalHenkiloDto = this.onrServiceClient.getHenkiloByHetu("hetu1");
        assertThat(optionalHenkiloDto).isEmpty();
    }

    @Test(expected = UnhandledHttpStatusCodeException.class)
    public void clientReceivesBadStatusCode() {
        CloseableHttpResponse httpResponse = this.mockResponse("", SC_BAD_REQUEST, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any())).willReturn(new OphHttpResponseImpl<>(httpResponse));
        this.onrServiceClient.getHenkiloByHetu("hetu1");
    }

    @Test
    public void retryIfKotikuntaInvalid() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setKotikunta("000");
        CloseableHttpResponse httpResponse = this.mockResponse(this.getJsonFromClasspath("/json/kotikunta_error.json"), SC_BAD_REQUEST, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any()))
                .willReturn(new OphHttpResponseImpl<>(httpResponse))
                .willReturn(new OphHttpResponseImpl<>(this.mockResponse("", SC_OK, ContentType.APPLICATION_JSON.toString())));
        this.onrServiceClient.updateHenkilo(henkiloForceUpdateDto, true);
        assertThat(henkiloForceUpdateDto.getKotikunta()).isEqualTo(KUNTAKOODI_TUNTEMATON);
    }

    @Test
    public void retryIfAidinkieliInvalid() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setAidinkieli(new KielisyysDto("fi", null));
        CloseableHttpResponse httpResponse = this.mockResponse(this.getJsonFromClasspath("/json/aidinkieli_error.json"), SC_BAD_REQUEST, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any()))
                .willReturn(new OphHttpResponseImpl<>(httpResponse))
                .willReturn(new OphHttpResponseImpl<>(this.mockResponse("", SC_OK, ContentType.APPLICATION_JSON.toString())));
        this.onrServiceClient.updateHenkilo(henkiloForceUpdateDto, true);
        assertThat(henkiloForceUpdateDto.getKotikunta()).isEqualTo(KIELIKOODI_TUNTEMATON);
    }

    @Test
    public void retryIfKansalaisuuskoodiInvalid() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        KansalaisuusDto kansalaisuusDto = new KansalaisuusDto();
        kansalaisuusDto.setKansalaisuusKoodi("KOSOVO");
        henkiloForceUpdateDto.setKansalaisuus(Sets.newHashSet(kansalaisuusDto));
        CloseableHttpResponse httpResponse = this.mockResponse(this.getJsonFromClasspath("/json/kansalaisuuskoodi_error.json"), SC_BAD_REQUEST, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any()))
                .willReturn(new OphHttpResponseImpl<>(httpResponse))
                .willReturn(new OphHttpResponseImpl<>(this.mockResponse("", SC_OK, ContentType.APPLICATION_JSON.toString())));
        this.onrServiceClient.updateHenkilo(henkiloForceUpdateDto, true);
        assertThat(henkiloForceUpdateDto.getKansalaisuus())
                .flatExtracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactly(KANSALAISUUSKOODI_TUNTEMATON);
    }

    @Test
    public void retryIfKansalaisuuskoodiAndKotikuntaAreInvalid() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        KansalaisuusDto kansalaisuusDto = new KansalaisuusDto();
        kansalaisuusDto.setKansalaisuusKoodi("KOSOVO");
        henkiloForceUpdateDto.setKansalaisuus(Sets.newHashSet(kansalaisuusDto));
        henkiloForceUpdateDto.setKotikunta("000");
        CloseableHttpResponse httpResponse = this.mockResponse(this.getJsonFromClasspath("/json/kotikunta_kansalaisuuskoodi_error.json"), SC_BAD_REQUEST, ContentType.APPLICATION_JSON.toString());
        given(this.ophHttpClient.execute(any()))
                .willReturn(new OphHttpResponseImpl<>(httpResponse))
                .willReturn(new OphHttpResponseImpl<>(this.mockResponse("", SC_OK, ContentType.APPLICATION_JSON.toString())));
        this.onrServiceClient.updateHenkilo(henkiloForceUpdateDto, true);
        assertThat(henkiloForceUpdateDto.getKansalaisuus())
                .flatExtracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactly(KANSALAISUUSKOODI_TUNTEMATON);
        assertThat(henkiloForceUpdateDto.getKotikunta()).isEqualTo(KUNTAKOODI_TUNTEMATON);
    }

    private String getJsonFromClasspath(String filename) {
        Resource jsonFile = new ClassPathResource(filename);
        try (InputStream jsonFileInputStream = jsonFile.getInputStream()){
            return IOUtils.toString(jsonFileInputStream, Charset.defaultCharset());
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private CloseableHttpResponse mockResponse(String json, int returnStatus, String contentType) {
        try {
            InputStream inputStream = new ByteArrayInputStream(json.getBytes());
            CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
            HttpEntity httpEntity = Mockito.mock(HttpEntity.class);
            given(httpResponse.getEntity()).willReturn(httpEntity);
            given(httpEntity.getContent()).willReturn(inputStream);

            StatusLine statusLine = Mockito.mock(StatusLine.class);
            given(httpResponse.getStatusLine()).willReturn(statusLine);
            given(statusLine.getStatusCode()).willReturn(returnStatus);

            Header[] headers = {new BasicHeader(CONTENT_TYPE, contentType)};
            given(httpResponse.getHeaders(eq(CONTENT_TYPE))).willReturn(headers);

            return httpResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
