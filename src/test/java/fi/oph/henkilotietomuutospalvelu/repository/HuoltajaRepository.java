package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface HuoltajaRepository extends Repository<Huoltaja, Long> {

    Optional<Huoltaja> findByHetu(String hetu);

}
