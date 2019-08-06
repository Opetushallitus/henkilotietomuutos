package fi.oph.henkilotietomuutospalvelu.repository;

import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;

import java.util.Optional;

public interface VtjDataRepositoryCustom {

    Optional<VtjDataEvent> findLatestByHetu(String hetu);

}
