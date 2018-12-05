package fi.oph.henkilotietomuutospalvelu.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HenkilotunnuskorjausRepositoryCustom {

    Set<String> findHetuByHenkilotunnuskorjausHetu(String hetu);

    Map<String, List<String>> findQueryHetuByHenkilotunnuskorjausHetu(Collection<String> hetut);

}
