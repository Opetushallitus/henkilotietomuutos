package fi.oph.henkilotietomuutospalvelu.repository;

import java.util.Set;

public interface HenkilotunnuskorjausRepositoryCustom {

    Set<String> findHetuByHenkilotunnuskorjausHetu(String hetu);

}
