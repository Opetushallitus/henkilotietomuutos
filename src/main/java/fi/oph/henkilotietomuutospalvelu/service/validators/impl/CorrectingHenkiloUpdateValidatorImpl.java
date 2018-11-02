package fi.oph.henkilotietomuutospalvelu.service.validators.impl;

import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.oph.henkilotietomuutospalvelu.service.validators.CorrectingHenkiloUpdateValidator;
import fi.oph.henkilotietomuutospalvelu.service.validators.UnknownKoodi;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.oph.henkilotietomuutospalvelu.service.validators.UnknownKoodi.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorrectingHenkiloUpdateValidatorImpl implements CorrectingHenkiloUpdateValidator {
    private final KoodistoService koodistoService;

    @Override
    public void validateAndCorrectErrors(HenkiloForceUpdateDto henkiloForceUpdateDto) {
        Stream.<Consumer<HenkiloForceUpdateDto>>of(
                (updateDto) -> Optional.ofNullable(updateDto.getKotikunta())
                        .ifPresent(kotikunta -> this.replaceIfInvalid(
                                this.koodistoService.isKoodiValid(Koodisto.KUNTA, kotikunta),
                                updateDto::setKotikunta,
                                kotikunta,
                                KUNTAKOODI_TUNTEMATON)),
                (updateDto) -> Optional.ofNullable(updateDto.getAidinkieli())
                        .ifPresent(aidinkieli -> this.replaceIfInvalid(
                                this.koodistoService.isKoodiValid(Koodisto.KIELI, aidinkieli.getKieliKoodi()),
                                aidinkieli::setKieliKoodi,
                                aidinkieli.getKieliKoodi(),
                                KIELIKOODI_TUNTEMATON)),
                (updateDto) -> Optional.ofNullable(updateDto.getKansalaisuus())
                        .ifPresent( kansalaisuudet ->
                                kansalaisuudet.forEach(kansalaisuusDto -> Optional.ofNullable(kansalaisuusDto.getKansalaisuusKoodi())
                                        .ifPresent(kansalaisuusKoodi -> this.replaceIfInvalid(
                                                this.koodistoService.isKoodiValid(Koodisto.MAAT_JA_VALTIOT_2, kansalaisuusKoodi),
                                                kansalaisuusDto::setKansalaisuusKoodi,
                                                kansalaisuusKoodi,
                                                KANSALAISUUSKOODI_TUNTEMATON)))),
                (updateDto) -> Optional.ofNullable(henkiloForceUpdateDto.getHuoltajat())
                        .ifPresent(huoltajat -> huoltajat.forEach(this::validateAndCorrectErrors))
        ).forEach(consumer -> consumer.accept(henkiloForceUpdateDto));
    }

    private void validateAndCorrectErrors(HuoltajaCreateDto huoltajaCreateDto) {
        Stream.<Consumer<HuoltajaCreateDto>>of(
                (updateDto) -> Optional.ofNullable(updateDto.getKansalaisuusKoodi())
                        .ifPresent(kansalaisuuskoodit ->
                                kansalaisuuskoodit.forEach(kansalaisuusKoodi -> this.replaceIfInvalid(
                                        (koodi) -> this.koodistoService.isKoodiValid(Koodisto.MAAT_JA_VALTIOT_2, koodi),
                                        updateDto::setKansalaisuusKoodi,
                                        updateDto.getKansalaisuusKoodi(),
                                        KANSALAISUUSKOODI_TUNTEMATON))),
                (updateDto) -> Optional.ofNullable(updateDto.getHuoltajuustyyppiKoodi())
                        .ifPresent(huoltajuustyyppi -> this.replaceIfInvalid(
                                this.koodistoService.isKoodiValid(Koodisto.HUOLTAJUUSTYYPPI, huoltajuustyyppi),
                                updateDto::setHuoltajuustyyppiKoodi,
                                huoltajuustyyppi,
                                HUOLTAJUUSTYYPPI_TUNTEMATON))
        ).forEachOrdered(consumer -> consumer.accept(huoltajaCreateDto));
    }

    private void replaceIfInvalid(Function<String, Boolean> isValidCheck,
                                  Consumer<Set<String>> setter,
                                  Collection<String> values,
                                  UnknownKoodi defaultValue) {
        Set<String> correctedValues = values.stream()
                .map(value -> isValidCheck.apply(value) ? value : defaultValue.getKoodi())
                .collect(Collectors.toSet());
        if (values.stream().noneMatch(isValidCheck::apply)) {
            log.warn("Replacing {} containing invalid koodi values with {}", values, correctedValues);
        }
        setter.accept(correctedValues);
    }

    private void replaceIfInvalid(boolean isValid, Consumer<String> setter, String value, UnknownKoodi defaultValue) {
        if (!isValid) {
            log.warn("Replacing invalid koodi value {} with default value {}", value, defaultValue.getKoodi());
            setter.accept(defaultValue.getKoodi());
        }
    }
}
