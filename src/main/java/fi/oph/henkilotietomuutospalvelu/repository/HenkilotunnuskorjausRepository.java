package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import org.springframework.data.repository.Repository;

public interface HenkilotunnuskorjausRepository extends Repository<Henkilotunnuskorjaus, Long>, HenkilotunnuskorjausRepositoryCustom {
}
