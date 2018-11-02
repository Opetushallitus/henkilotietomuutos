package fi.oph.henkilotietomuutospalvelu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "koodisto")
public class KoodistoServiceImpl implements KoodistoService {

    private final OphHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Cacheable
    @Override
    public List<KoodiDto> list(Koodisto koodisto) {
        KoodiDto[] koodit = httpClient.get("koodisto-service.koodi", koodisto.getUri())
                .expectStatus(200)
                .accept(JSON)
                .retryOnError(3)
                .execute(response -> objectMapper.readValue(response.asInputStream(), KoodiDto[].class));
        return Arrays.stream(koodit).collect(Collectors.toList());
    }

    @Override
    public Map<String, KoodiDto> listAsMap(Koodisto koodisto) {
        return this.list(koodisto).stream()
                .collect(Collectors.toMap(KoodiDto::getKoodiArvo, Function.identity()));
    }

}
