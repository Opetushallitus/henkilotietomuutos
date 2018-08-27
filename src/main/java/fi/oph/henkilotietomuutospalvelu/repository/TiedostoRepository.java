package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface TiedostoRepository extends CrudRepository<Tiedosto, Long> {
    Optional<Tiedosto> findByFileName(String fileName);
}
