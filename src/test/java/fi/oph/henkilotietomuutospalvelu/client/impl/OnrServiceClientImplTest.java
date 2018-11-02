package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.config.UrlConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.properties.CasProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.OppijanumerorekisteriProperties;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpResponseImpl;
import fi.vm.sade.javautils.http.exceptions.UnhandledHttpStatusCodeException;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.Header.CONTENT_TYPE;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

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
