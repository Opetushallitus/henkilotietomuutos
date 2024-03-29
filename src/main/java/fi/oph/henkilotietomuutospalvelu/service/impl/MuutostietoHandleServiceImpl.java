package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiMetadataDto;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoAlkupera;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.HenkilotunnuskorjausRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TietoryhmaRepository;
import fi.oph.henkilotietomuutospalvelu.service.*;
import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import fi.oph.henkilotietomuutospalvelu.service.validators.CorrectingHenkiloUpdateValidator;
import fi.oph.henkilotietomuutospalvelu.utils.CustomOrderComparator;
import fi.oph.henkilotietomuutospalvelu.utils.HenkiloMuutostietoRiviComparator;
import fi.oph.henkilotietomuutospalvelu.utils.HenkiloUtils;
import fi.oph.henkilotietomuutospalvelu.utils.TiedostoComparator;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static fi.oph.henkilotietomuutospalvelu.utils.YhteystietoUtils.removeYhteystietoryhma;
import static java.util.Collections.singleton;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MuutostietoHandleServiceImpl implements MuutostietoHandleService {
    private final OnrServiceClient onrServiceClient;
    private final KoodistoService koodistoService;
    private final VtjService vtjService;
    private final FileService fileService;
    private final TimeService timeService;

    private final HenkiloMuutostietoRepository henkiloMuutostietoRepository;
    private final TietoryhmaRepository tietoryhmaRepository;
    private final HenkilotunnuskorjausRepository henkilotunnuskorjausRepository;
    private final TiedostoRepository tiedostoRepository;

    private final CorrectingHenkiloUpdateValidator correctingHenkiloUpdateValidator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void importUnprocessedMuutostiedotToDb(List<MuutostietoDto> muutostietoDtoList, String fileName) {
        Tiedosto tiedosto = this.tiedostoRepository.findByFileName(fileName)
                .orElseThrow(() -> new IllegalStateException(String.format("Provided file %s was not found in db", fileName)));
        Optional<Integer> lastProcessedRow = this.henkiloMuutostietoRepository.findLastRowByTiedostoNimi(fileName);

        List<HenkiloMuutostietoRivi> unprocessedHenkiloMuutostietoRiviList = muutostietoDtoList.stream()
                .filter(muutostieto -> !lastProcessedRow.isPresent() || muutostieto.getRivi() > lastProcessedRow.get())
                .map(this::mapMuutostietoToHenkiloMuutostietoRivi)
                .collect(Collectors.toList());
        unprocessedHenkiloMuutostietoRiviList.forEach(henkiloMuutostietoRivi -> henkiloMuutostietoRivi.setTiedosto(tiedosto));
        this.henkiloMuutostietoRepository.saveAll(unprocessedHenkiloMuutostietoRiviList);
    }

    private HenkiloMuutostietoRivi mapMuutostietoToHenkiloMuutostietoRivi(MuutostietoDto muutostietoDto) {
        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setRivi(muutostietoDto.getRivi());
        henkiloMuutostietoRivi.setQueryHetu(muutostietoDto.getHetu());
        List<Tietoryhma> tietoryhmaList = new ArrayList<>(muutostietoDto.getTietoryhmat());
        tietoryhmaList.forEach(tietoryhma -> tietoryhma.setHenkiloMuutostietoRivi(henkiloMuutostietoRivi));
        this.tietoryhmaRepository.saveAll(tietoryhmaList);
        henkiloMuutostietoRivi.setTietoryhmaList(tietoryhmaList);
        if (muutostietoDto.getRivi() % 1000 == 0) {
            log.info("Added {} lines to db", muutostietoDto.getRivi());
        }
        return henkiloMuutostietoRivi;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleMuutostieto(HenkiloMuutostietoRivi henkiloMuutostietoRivi) {
        log.info("Handling muutostietorivi {}", henkiloMuutostietoRivi.getId());
        if (henkiloMuutostietoRivi.getProcessTimestamp() != null) {
            return;
        }
        Optional<HenkiloForceReadDto> currentHenkiloOptional = this.getCurrentHenkilo(henkiloMuutostietoRivi.getQueryHetu());
        currentHenkiloOptional.ifPresent(currentHenkilo -> {
            log.info("Found henkilo {} for muutostietorivi {}", currentHenkilo.getOidHenkilo(), henkiloMuutostietoRivi.getId());
            Set<String> kaikkiHetut = getKaikkiHetut(currentHenkilo.getHetu());
            log.info("Found {} hetus for henkilo {}", kaikkiHetut.size(), currentHenkilo.getOidHenkilo());
            Map<Tiedosto, List<HenkiloMuutostietoRivi>> kaikkiMuutostietoRivit = henkiloMuutostietoRepository
                    .findByQueryHetuInAndProcessTimestampIsNull(kaikkiHetut)
                    .stream()
                    .collect(groupingBy(HenkiloMuutostietoRivi::getTiedosto));

            HenkiloForceUpdateDto updateHenkilo = new HenkiloForceUpdateDto();
            updateHenkilo.setOidHenkilo(currentHenkilo.getOidHenkilo());
            // oppijanumerorekisterissä saattaa olla vanhaa tietoa turvakiellon osalta
            // -> jos käsitellään perustietoaineistosta lähtien, asetetaan oletuksena turvakielto pois päältä
            // -> jos käsitellään vain muutostietoaineistoja, pidetään oletuksena nykyinen arvo
            if (kaikkiMuutostietoRivit.keySet().stream().anyMatch(Tiedosto::isPerustietoaineisto)) {
                updateHenkilo.setTurvakielto(false);
            }
            updateHenkilo.setYhteystiedotRyhma(currentHenkilo.getYhteystiedotRyhma());
            updateHenkilo.setHuoltajat(currentHenkilo.getHuoltajat());

            if (!currentHenkilo.isPassivoitu()) {
                TietoryhmaContextImpl tietoryhmaContext = new TietoryhmaContextImpl(currentHenkilo, this.koodistoService, this.timeService);
                kaikkiMuutostietoRivit.entrySet().stream()
                        // käsitellään tiedostot vanhimmasta uusimpaan
                        .sorted(comparingByKey(new TiedostoComparator(fileService)))
                        .map(Map.Entry::getValue)
                        .forEach(muutostietoRivit -> {
                            Boolean turvakielto = updateHenkilo.getTurvakielto();
                            muutostietoRivit.stream()
                                    .sorted(new HenkiloMuutostietoRiviComparator())
                                    .flatMap(HenkiloMuutostietoRivi::getTietoryhmaStream)
                                    .sorted(Comparator
                                            // käsitellään poistot ensin koska samassa tiedostossa voi olla sekä poistoja
                                            // että korjauksia samoihin yhteystietotyyppeihin
                                            .comparing(Tietoryhma::getMuutostapa, new CustomOrderComparator<>(Muutostapa.POISTETTU))
                                            // käsitellään voimassaolevat viimeiseksi koska samassa tiedostossa voi olla
                                            // sekä muokkauksia (esim. passivointi) että lisäyksiä samoihin yhteystietoihin
                                            .thenComparing(tietoryhma -> tietoryhma.isVoimassa(tietoryhmaContext)))
                                    .forEach(tietoryhma -> tietoryhma.updateHenkilo(tietoryhmaContext, updateHenkilo));
                            if (Boolean.TRUE.equals(updateHenkilo.getTurvakielto()) && !Boolean.TRUE.equals(turvakielto)) {
                                // turvakielto meni päälle tässä tiedostossa -> poistetaan muutostietopalvelun alaiset yhteystiedot
                                removeYhteystietoryhma(updateHenkilo.getYhteystiedotRyhma(), KoodistoYhteystietoAlkupera.VTJ);
                            }
                        });
                if (updateHenkilo.getEtunimet() != null || updateHenkilo.getKutsumanimi() != null) {
                    // etunimet ja/tai kutsumanimi muuttui -> validoidaan kutsumanimi
                    String etunimet = Optional.ofNullable(updateHenkilo.getEtunimet()).orElse(currentHenkilo.getEtunimet());
                    String kutsumanimi = Optional.ofNullable(updateHenkilo.getKutsumanimi()).orElse(currentHenkilo.getKutsumanimi());

                    if (!HenkiloUpdateUtil.isValidKutsumanimi(etunimet, kutsumanimi)) {
                        updateHenkilo.setKutsumanimi(etunimet);
                    }
                }
                updateHenkilo.setKaikkiHetut(kaikkiHetut);
                this.vtjService.yksiloiHuoltajatTarvittaessa(updateHenkilo);
                this.correctingHenkiloUpdateValidator.validateAndCorrectErrors(updateHenkilo);
                this.onrServiceClient.updateHenkilo(updateHenkilo, true);
            }
            else {
                log.error("Henkilo '{}' has already been passivoitu and cannot be further modified.", currentHenkilo.getOidHenkilo());
            }
            kaikkiMuutostietoRivit.values()
                    .stream()
                    .flatMap(List::stream)
                    .forEach(this::updateProcessTimestamp);
        });
        if (!currentHenkiloOptional.isPresent()) {
            log.info("Henkilo not found for muutostietorivi {} from oppijanumerorekisteri", henkiloMuutostietoRivi.getId());
            updateProcessTimestamp(henkiloMuutostietoRivi);
        }
    }

    private Set<String> getKaikkiHetut(String hetu) {
        Set<String> kaikkiHetut = henkilotunnuskorjausRepository.findHetuByHenkilotunnuskorjausHetu(hetu);
        if (kaikkiHetut.isEmpty()) {
            // henkilöllä ei ole ollut muita hetuja
            return singleton(hetu);
        }
        // henkilöllä on ollut muita hetuja -> tarkistetaan ettei vanha hetu ole ollut kenenkään muun henkilön käytössä
        return henkilotunnuskorjausRepository.findQueryHetuByHenkilotunnuskorjausHetu(kaikkiHetut)
                .entrySet()
                .stream()
                .filter(entry -> kaikkiHetut.containsAll(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
    }

    private void updateProcessTimestamp(HenkiloMuutostietoRivi henkiloMuutostietoRivi) {
        henkiloMuutostietoRivi.setProcessTimestamp(timeService.getLocalDateTime());
        this.henkiloMuutostietoRepository.save(henkiloMuutostietoRivi);
    }

    @Getter
    @RequiredArgsConstructor
    private static class TietoryhmaContextImpl implements Tietoryhma.Context {

        private final HenkiloForceReadDto currentHenkilo;
        private final KoodistoService koodistoService;
        private final TimeService timeService;

        private static Optional<String> getKoodiNimi(Map<String, KoodiDto> koodit, String koodiArvo, String kieli) {
            return Optional.ofNullable(koodit.get(koodiArvo))
                    .flatMap(koodi -> koodi.getMetadata().stream()
                            .sorted(Comparator.comparing(metadata -> metadata.getKieli().trim().toLowerCase(),
                                    new CustomOrderComparator<>(kieli, HenkiloUtils.KIELIKOODI_FI)))
                            .map(KoodiMetadataDto::getNimi)
                            .filter(StringUtils::hasLength)
                            .findFirst());
        }

        @Override
        public Optional<String> getPostitoimipaikka(String postinumero, String kieli) {
            return getKoodiNimi(this.koodistoService.listAsMap(Koodisto.POSTI), postinumero, kieli);
        }

        @Override
        public Optional<String> getMaa(String maakoodi, String kieli) {
            return getKoodiNimi(this.koodistoService.listAsMap(Koodisto.MAAT_JA_VALTIOT_2), maakoodi, kieli);
        }

        @Override
        public LocalDate getLocalDateNow() {
            return timeService.getLocalDate();
        }
    }

    @NotNull
    private Optional<HenkiloForceReadDto> getCurrentHenkilo(String queryHetu) {
        Optional<HenkiloForceReadDto> optionalHenkiloDto = this.onrServiceClient.getHenkiloByHetu(queryHetu);
        if (!optionalHenkiloDto.isPresent()) {
            // Trying to find henkilo with changed hetus.
            List<HenkiloMuutostietoRivi> allCurrentHenkiloRivis = this.henkiloMuutostietoRepository
                    .findHenkiloMuutostietoRiviByQueryHetu(queryHetu);

            optionalHenkiloDto = allCurrentHenkiloRivis.stream()
                    .flatMap(henkiloMuutostietoRivi -> henkiloMuutostietoRivi.getTietoryhmaList().stream())
                    .filter(Henkilotunnuskorjaus.class::isInstance)
                    .map(Henkilotunnuskorjaus.class::cast)
                    .filter(henkilotunnuskorjaus -> !queryHetu.equals(henkilotunnuskorjaus.getHetu()))
                    .map(Henkilotunnuskorjaus::getHetu)
                    .distinct()
                    .map(this.onrServiceClient::getHenkiloByHetu)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
            if (optionalHenkiloDto.isPresent()) {
                // Update correct query_hetu
                String newHetu = optionalHenkiloDto.get().getHetu();
                allCurrentHenkiloRivis.forEach(currentHenkiloRivi -> currentHenkiloRivi.setQueryHetu(newHetu));
            }
            else {
                log.error("Henkilo {} not found from oppijanumerorekisteri and no other known hetus match. Can not update.", queryHetu);
            }
        }
        return optionalHenkiloDto;
    }
}
