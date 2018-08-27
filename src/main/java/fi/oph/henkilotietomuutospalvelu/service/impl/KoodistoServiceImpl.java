package fi.oph.henkilotietomuutospalvelu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;

@Service
@RequiredArgsConstructor
public class KoodistoServiceImpl implements KoodistoService {

    private final OphHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<KoodiDto> list(Koodisto koodisto) {
        KoodiDto[] koodit = httpClient.get("koodisto-service.koodi", koodisto.getUri())
                .expectStatus(200)
                .accept(JSON)
                .retryOnError(3)
                .execute(response -> objectMapper.readValue(response.asInputStream(), KoodiDto[].class));
        return Arrays.stream(koodit).collect(Collectors.toList());
    }

}
