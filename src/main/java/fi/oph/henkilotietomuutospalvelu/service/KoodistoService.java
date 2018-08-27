package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;

import java.util.List;

public interface KoodistoService {

    List<KoodiDto> list(Koodisto koodisto);

}
