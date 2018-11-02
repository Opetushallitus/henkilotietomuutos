package fi.oph.henkilotietomuutospalvelu.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.client.KoodistoClient;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = "koodisto")
public class KoodistoClientImpl implements KoodistoClient {
    private final OphHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable
    public List<KoodiDto> list(Koodisto koodisto) {
        KoodiDto[] koodit = httpClient.get("koodisto-service.koodi", koodisto.getUri())
                .expectStatus(200)
                .accept(JSON)
                .retryOnError(3)
                .execute(response -> objectMapper.readValue(response.asInputStream(), KoodiDto[].class));
        return Arrays.stream(koodit).collect(Collectors.toList());
    }
}
