package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.KoodistoClient;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KoodistoServiceImpl implements KoodistoService {
    private final KoodistoClient koodistoClient;

    @Override
    public List<KoodiDto> list(Koodisto koodisto) {
        return this.koodistoClient.list(koodisto);
    }

    @Override
    public Map<String, KoodiDto> listAsMap(Koodisto koodisto) {
        return this.list(koodisto).stream()
                .collect(Collectors.toMap(KoodiDto::getKoodiArvo, Function.identity()));
    }

    @Override
    public boolean isKoodiValid(Koodisto koodisto, String koodi) {
        if (StringUtils.isEmpty(koodi)) {
            return false;
        }
        return this.list(koodisto).stream()
                .map(KoodiDto::getKoodiArvo)
                .map(String::toLowerCase)
                .anyMatch(kuntaKoodi -> kuntaKoodi.equals(koodi.toLowerCase()));
    }

}
