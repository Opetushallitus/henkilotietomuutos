package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;

import java.util.List;
import java.util.Optional;

public interface HenkiloMuutostietoRepositoryCustom {
    List<String> findDistinctUnprocessedTiedostoFileName();

    Optional<Integer> findLastRowByTiedostoNimi(String fileName);

    List<HenkiloMuutostietoRivi> findHenkiloMuutostietoRiviByQueryHetu(String queryHetu);

    List<Long> findByTiedostoFileNameAndProcessTimestampIsNullOrderByRivi(String fileName);


}
