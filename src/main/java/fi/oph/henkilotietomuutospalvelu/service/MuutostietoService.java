package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;

import java.io.IOException;
import java.util.List;

public interface MuutostietoService {
    /**
     * Read basic info (.PTT) or change info (.MTT) from file and update the content to oppijanumerorekisteri
     * @return Changes the file contains
     * @throws IOException anything related to file handling
     * @param lastHandledLineNumber Last line number of the last handled muutostieto. Will be set to 0 if new file.
     */
    List<MuutostietoDto> importMuutostiedot(int lastHandledLineNumber) throws IOException;

    /**
     * Update unhandled muutostiedot from database to oppijanumerorekisteri-service.
     */
    void updateMuutostietos();

    /**
     * Update all unhandled muutostiedot from database to oppijanumerorekisteri-service.
     */
    void updateAllMuutostietos();

    /**
     * Download basic or change info through SFTP
     * @return file names
     * @throws IOException anything related to file handling
     */
    List<String> downloadFiles() throws IOException;

}
