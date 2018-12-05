package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface HenkiloMuutostietoRepository extends CrudRepository<HenkiloMuutostietoRivi, Long>, HenkiloMuutostietoRepositoryCustom {
    List<HenkiloMuutostietoRivi> findByTiedostoFileNameAndProcessTimestampIsNullOrderByRivi(String fileName);

    List<HenkiloMuutostietoRivi> findByQueryHetuIn(Collection<String> hetu);

}
