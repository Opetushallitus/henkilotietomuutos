package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TiedostoRepository extends CrudRepository<Tiedosto, Long> {
    Optional<Tiedosto> findByFileName(String fileName);
}
