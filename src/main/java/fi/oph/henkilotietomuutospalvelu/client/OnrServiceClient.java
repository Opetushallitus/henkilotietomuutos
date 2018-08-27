package fi.oph.henkilotietomuutospalvelu.client;

import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;

import java.util.Optional;

public interface OnrServiceClient {

    /**
     * Update henkilo to oppijanumerorekisteri using patch like method
     * @param updateDto update info
     * @param retry in case kielikoodi is invalid try again with default one (unknown)
     */
    void updateHenkilo(HenkiloForceUpdateDto updateDto, boolean retry);

    Optional<HenkiloDto> getHenkiloByHetu(String hetu);

}