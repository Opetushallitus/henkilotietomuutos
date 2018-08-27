package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.UrlConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.properties.CasProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.OppijanumerorekisteriProperties;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kansalaisuus;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import fi.vm.sade.javautils.http.auth.CasAuthenticator;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KansalaisuusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Aidinkieli.KIELIKOODI_TUNTEMATON;
import static fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Kotikunta.KUNTAKOODI_TUNTEMATON;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnrServiceClientImpl implements OnrServiceClient {

    private final UrlConfiguration urlConfiguration;
    private final ObjectMapper objectMapper;

    private final CasProperties casProperties;
    private final OppijanumerorekisteriProperties oppijanumerorekisteriProperties;

    private OphHttpClient ophHttpClient;

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    @PostConstruct
    public void setup() {
        CasAuthenticator casAuthenticator = new CasAuthenticator.Builder()
                .username(this.oppijanumerorekisteriProperties.getUsername())
                .password(this.oppijanumerorekisteriProperties.getPassword())
                .webCasUrl(this.casProperties.getUrl())
                .casServiceUrl(urlConfiguration.url("oppijanumerorekisteri-service.security-check"))
                .build();

        ophHttpClient = new OphHttpClient.Builder("HenkilotietomuutosPalvelu")
                .authenticator(casAuthenticator)
                .build();
    }

    @Override
    public void updateHenkilo(HenkiloForceUpdateDto updateDto, boolean retry) {
        String content;
        try {
            content = this.objectMapper.writeValueAsString(updateDto);
        }
        catch (JsonProcessingException e) {
            throw new RestClientException("Error mapping to json", e);
        }
        if (Boolean.FALSE.equals(this.oppijanumerorekisteriProperties.getNoUpdateMode())) {
            OphHttpRequest request = OphHttpRequest.Builder
                    .put(urlConfiguration.url("oppijanumerorekisteri-service.s2s.henkilo"))
                    .addHeader("Content-Type", CONTENT_TYPE)
                    .setEntity(new OphHttpEntity.Builder()
                            .content(content)
                            .contentType(ContentType.APPLICATION_JSON)
                            .build())
                    .build();
            ophHttpClient.execute(request)
                    .handleErrorStatus(HttpServletResponse.SC_BAD_REQUEST)
                    .with(responseAsString -> {
                        if (retry && this.updateIfValidationErrorCanBeHandled(responseAsString, updateDto)) {
                            this.updateHenkilo(updateDto, false);
                            return Optional.empty();
                        }
                        throw new RestClientException(responseAsString);
                    })
                    .expectedStatus(SC_OK)
                    .ignoreResponse();
        }
        else {
            log.info("No update mode is active. Not updating henkilo {}", updateDto.getOidHenkilo());
        }
    }

    private boolean updateIfValidationErrorCanBeHandled(String responseAsString, HenkiloForceUpdateDto updateDto) {
        boolean didHandleError = false;
        if (responseAsString.contains("\"invalid.kansalaisuusKoodi\"")) {
            log.warn("VTJ provided invalid kansalaisuuskoodi {} for henkilo {}, retrying with koodi {}",
                    updateDto.getKansalaisuus().stream().map(KansalaisuusDto::getKansalaisuusKoodi).collect(Collectors.toSet()),
                    updateDto.getOidHenkilo(),
                    Kansalaisuus.KANSALAISUUSKOODI_TUNTEMATON);
            KansalaisuusDto unknownKansalaisuus = new KansalaisuusDto();
            unknownKansalaisuus.setKansalaisuusKoodi(Kansalaisuus.KANSALAISUUSKOODI_TUNTEMATON);
            updateDto.setKansalaisuus(Sets.newHashSet(unknownKansalaisuus));
            didHandleError = true;
        }
        if (responseAsString.contains("\"invalid.kotikunta\"")) {
            log.warn("VTJ provided invalid kuntakoodi {} for henkilo {}, retrying with koodi {}",
                    updateDto.getKotikunta(), updateDto.getOidHenkilo(), KUNTAKOODI_TUNTEMATON);
            updateDto.setKotikunta(KUNTAKOODI_TUNTEMATON);
            didHandleError = true;
        }
        if (responseAsString.contains("\"invalid.aidinkieli\"")) {
            log.warn("VTJ provided invalid aidinkieli {} for henkilo {}, retrying with koodi {}",
                    updateDto.getKotikunta(), updateDto.getOidHenkilo(), KIELIKOODI_TUNTEMATON);
            updateDto.setKotikunta(KIELIKOODI_TUNTEMATON);
            didHandleError = true;
        }

        return didHandleError;
    }

    @Override
    public Optional<HenkiloDto> getHenkiloByHetu(String hetu) {
        OphHttpRequest request = OphHttpRequest.Builder
                .get(urlConfiguration.url("oppijanumerorekisteri-service.henkilo.hetu", hetu))
                .build();
        return ophHttpClient.<HenkiloDto>execute(request)
                .expectedStatus(SC_OK).mapWith(text -> {
                    try {
                        return this.objectMapper.readValue(text, HenkiloDto.class);
                    } catch (IOException jpe) {
                        throw new RestClientException(jpe.getMessage());
                    }
                });
    }
}
