package fi.oph.henkilotietomuutospalvelu.service.validators;

import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;

public interface CorrectingHenkiloUpdateValidator {
    /**
     * Korjaa havaitut virheelliset arvot
     * @param henkiloForceUpdateDto Dto henkilön tietojen päivitykseen
     */
    void validateAndCorrectErrors(HenkiloForceUpdateDto henkiloForceUpdateDto);
}
