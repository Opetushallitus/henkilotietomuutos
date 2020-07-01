package fi.oph.henkilotietomuutospalvelu.config.scheduling;

import fi.oph.henkilotietomuutospalvelu.config.properties.SchedulingProperties;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.service.HetuService;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoService;
import fi.oph.henkilotietomuutospalvelu.service.exception.MuutostietoFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Ajastusten konfigurointi.
 *
 * @see SchedulingConfiguration ajastuksen aktivointi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final MuutostietoService muutostietoService;
    private final HetuService hetuService;

    private final SchedulingProperties schedulingProperties;

    private final KoodistoService koodistoService;

    @Scheduled(fixedDelayString = "${scheduling.fixed-delay-in-millis.downloading}", initialDelay = 3500)
    public void downloadMuutostiedot() {
        if (Boolean.TRUE.equals(this.schedulingProperties.getEnable().getDownloading())) {
            this.downloadFiles();
        }
        if (Boolean.TRUE.equals(this.schedulingProperties.getEnable().getImporting())) {
            this.importAllChanges();
        }
    }

    @Scheduled(fixedDelayString = "${scheduling.fixed-delay-in-millis.handling}", initialDelay = 7500)
    public void updateMuutostiedotToONR() {
        if (Boolean.TRUE.equals(this.schedulingProperties.getEnable().getHandling())) {
            this.handleChanges();
        }
    }

    @Scheduled(cron = "${scheduling.cron.hetu-update: 0 0 21 * * ?}")
    public void updateAllHetusToSftpServer() {
        if (Boolean.TRUE.equals(this.schedulingProperties.getEnable().getHetuUpdate())) {
            log.info("Starting transferring hetus to sftp server");
            this.hetuService.updateHetusToVtj();
            log.info("Completed transferring hetus to sftp server");
        }
    }

    private void importAllChanges() {
        long start = System.currentTimeMillis();
        log.info("Start importing muutostietos from files to database.");
        int lastHandledLineNumber = 0;

        for (int i = 0; ; i++) {
            List<MuutostietoDto> importedMuutostietoDtos = this.importChanges(lastHandledLineNumber);
            lastHandledLineNumber = importedMuutostietoDtos.stream()
                    .mapToInt(MuutostietoDto::getRivi)
                    .max()
                    .orElse(0);
            if (lastHandledLineNumber == 0) {
                break;
            }
            if (i > 10000) {
                log.error("Exceeded {} imports. Possible infinite loop.", i);
                throw new RuntimeException("Possible infinite loop");
            }
        }
        long duration = System.currentTimeMillis() - start;
        log.info("Import completed, duration: {} ms.", duration);
    }

    private List<MuutostietoDto> importChanges(int lastHandledLineNumber) {

        try {
            List<MuutostietoDto> muutostiedot = this.muutostietoService.importMuutostiedot(lastHandledLineNumber);
            log.info("Imported {} muutostietoa to db.", muutostiedot.size());
            return muutostiedot;
        } catch (IOException | MuutostietoFileException e) {
            log.error("Failed to import changes.", e);
            return Collections.emptyList();
        }
    }

    private void handleChanges() {
        log.info("Start handling henkilo muutostietos from database to oppijanumerorekisteri");

        long start = System.currentTimeMillis();

        this.muutostietoService.updateMuutostietos();

        long duration = System.currentTimeMillis() - start;
        log.info("Handling completed, duration: {} ms.", duration);
    }

    private void downloadFiles() {
        log.info("Start downloading perus- and muutostietofiles from Tieto BIX.");
        long start = System.currentTimeMillis();
        List<String> files;
        try {
            files = this.muutostietoService.downloadFiles();
            log.info("Downloaded {} files.", files.size());

            long duration = System.currentTimeMillis() - start;
            log.info("Download completed, duration: {} ms.", duration);
        } catch (IOException e) {
            log.error("Failed to download files.", e);
        }
    }

}
