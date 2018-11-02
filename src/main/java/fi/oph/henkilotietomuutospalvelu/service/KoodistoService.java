package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;

import java.util.List;
import java.util.Map;

public interface KoodistoService {

    List<KoodiDto> list(Koodisto koodisto);

    Map<String, KoodiDto> listAsMap(Koodisto koodisto);

    boolean isKoodiValid(Koodisto koodisto, String koodi);
}
