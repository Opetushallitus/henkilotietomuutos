package fi.oph.henkilotietomuutospalvelu.client;

import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;

import java.util.Optional;

public interface VtjServiceClient {

    /**
     * Hakee henkilön VTJ tiedot hetun perusteella
     * @param hetu Henkilön hetu
     * @return Henkilön tiedot
     */
    Optional<YksiloityHenkilo> getHenkiloByHetu(String hetu);

}