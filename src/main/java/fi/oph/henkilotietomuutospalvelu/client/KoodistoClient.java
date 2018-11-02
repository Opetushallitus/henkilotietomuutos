package fi.oph.henkilotietomuutospalvelu.client;

import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;

import java.util.List;

public interface KoodistoClient {
    /**
     * Listaa kaikki koodiston koodit
     * @param koodisto Koodiston nimi
     * @return Halutun koodiston koodit
     */
    List<KoodiDto> list(Koodisto koodisto);
}
