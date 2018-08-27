package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface VtjDataRepository extends CrudRepository<VtjDataEvent, Long> {
    List<VtjDataEvent> findByVtjdataTimestampIsNull();
}
