package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.client.RyhmasahkopostiClient;
import fi.oph.henkilotietomuutospalvelu.config.ConfigEnums;
import fi.oph.henkilotietomuutospalvelu.config.UrlConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.properties.CasProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.ViestintaProperties;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import fi.vm.sade.javautils.http.auth.CasAuthenticator;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;

import static org.apache.http.HttpStatus.SC_OK;

@Component
@Slf4j
@RequiredArgsConstructor
public class RyhmasahkopostiClientImpl implements RyhmasahkopostiClient {
    private final UrlConfiguration urlConfiguration;
    private final ObjectMapper objectMapper;

    private final CasProperties casProperties;
    private final ViestintaProperties viestintaProperties;

    private OphHttpClient ophHttpClient;

    @PostConstruct
    public void setup() {
        CasAuthenticator casAuthenticator = new CasAuthenticator.Builder()
                .username(this.viestintaProperties.getUsername())
                .password(this.viestintaProperties.getPassword())
                .webCasUrl(this.casProperties.getUrl())
                .casServiceUrl(urlConfiguration.url("ryhmasahkoposti-service.security-check"))
                .build();

        ophHttpClient = new OphHttpClient.Builder(ConfigEnums.CALLER_ID.value())
                .authenticator(casAuthenticator)
                .build();
    }

    @Override
    public void sendRyhmasahkoposti(EmailData emailData) {
        String url = this.urlConfiguration.url("ryhmasahkoposti-service.email");
        try {
            String json = this.objectMapper.writeValueAsString(emailData);
            OphHttpRequest httpRequest = OphHttpRequest.Builder
                    .post(url)
                    .addHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                    .setEntity(new OphHttpEntity.Builder()
                            .content(json)
                            .contentType(ContentType.APPLICATION_JSON)
                            .build())
                    .build();
            ophHttpClient.execute(httpRequest)
                    .expectedStatus(SC_OK)
                    .ignoreResponse();
        } catch (JsonProcessingException jpe) {
            throw new RestClientException("Error mapping to json", jpe);
        }
    }

}
