package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.client.VtjServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.ConfigEnums;
import fi.oph.henkilotietomuutospalvelu.config.UrlConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.properties.CasProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.OppijanumerorekisteriProperties;
import fi.oph.henkilotietomuutospalvelu.utils.HenkiloUtils;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import fi.vm.sade.javautils.http.auth.CasAuthenticator;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@Slf4j
@Component
@RequiredArgsConstructor
public class VtjServiceClientImpl implements VtjServiceClient {
    private final UrlConfiguration urlConfiguration;
    private final ObjectMapper objectMapper;

    private final CasProperties casProperties;
    private final OppijanumerorekisteriProperties oppijanumerorekisteriProperties;

    private OphHttpClient ophHttpClient;

    @PostConstruct
    public void setup() {
        CasAuthenticator casAuthenticator = new CasAuthenticator.Builder()
                .username(this.oppijanumerorekisteriProperties.getUsername())
                .password(this.oppijanumerorekisteriProperties.getPassword())
                .webCasUrl(this.casProperties.getUrl())
                .casServiceUrl(urlConfiguration.url("vtj-service.security-check"))
                .build();

        ophHttpClient = new OphHttpClient.Builder(ConfigEnums.CALLER_ID.value())
                .authenticator(casAuthenticator)
                .build();
    }

    @Override
    public Optional<YksiloityHenkilo> getHenkiloByHetu(String hetu) {
        OphHttpRequest request = OphHttpRequest.Builder
                .get(urlConfiguration.url("vtj-service.henkilo.hetu", hetu))
                .build();
        Optional<YksiloityHenkilo> yksiloityHenkilo = ophHttpClient.<YksiloityHenkilo>execute(request)
                .expectedStatus(SC_OK).mapWith(text -> {
                    try {
                        return this.objectMapper.readValue(text, YksiloityHenkilo.class);
                    } catch (IOException ioe) {
                        throw new RestClientException(ioe.getMessage());
                    }
                });
        if (!yksiloityHenkilo.isPresent()) {
            log.warn("Could not find henkilo from VTJ with hetu {}", HenkiloUtils.sensuroiHetu(hetu));
        }
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException ie) {
            log.warn("Sleep was interrupted", ie);
        }
        return yksiloityHenkilo;
    }
}
