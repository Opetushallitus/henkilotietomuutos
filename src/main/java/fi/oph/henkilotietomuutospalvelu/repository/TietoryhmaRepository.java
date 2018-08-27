package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface TietoryhmaRepository extends CrudRepository<Tietoryhma, Long> {

}
