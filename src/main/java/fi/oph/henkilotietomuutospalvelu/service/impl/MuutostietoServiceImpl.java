package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.MuutosType;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.service.FileService;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoHandleService;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoParseService;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoService;
import fi.oph.henkilotietomuutospalvelu.service.exception.MuutostietoFileException;
import fi.oph.henkilotietomuutospalvelu.service.exception.MuutostietoLineParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MuutostietoServiceImpl implements MuutostietoService {

    private final FileService fileService;
    private final MuutostietoParseService muutostietoParseService;
    private final MuutostietoHandleService muutostietoHandleService;

    private final HenkiloMuutostietoRepository henkiloMuutostietoRepository;
    private final TiedostoRepository tiedostoRepository;

    @NotifyOnError(NotifyOnError.NotifyType.IMPORT)
    @Transactional(readOnly = true)
    @Override
    public List<MuutostietoDto> importMuutostiedot(int lastHandledLineNumber)
            throws IOException, MuutostietoFileException {
        Optional<String> optional = this.fileService.findNextFile();
        if (optional.isPresent()) {
            try {
                return handleFile(optional.get(), lastHandledLineNumber);
            } catch (MuutostietoFileException e) {
                log.error("Handling muutostieto file failed", e);
                throw e;
            }
        }
        return Collections.emptyList();
    }

    // Give each line a number (multi lines are considered as one),  and import to db
    private List<MuutostietoDto> handleFile(String filePath, int lastHandledLineNumber)
            throws MuutostietoFileException {
        Path path = Paths.get(filePath);
        AtomicInteger lineNumberCounter;
        String fileName;
        if (FilenameUtils.getExtension(path.toString()).equals("PART")) {
            fileName = FileServiceImpl.getOriginalNameFromPath(path.toString());
            lineNumberCounter = new AtomicInteger(lastHandledLineNumber);
        }
        else {
            fileName = FilenameUtils.getName(path.toString());
            try {
                path = this.fileService.splitFile(path);
            } catch (IOException e) {
                throw new MuutostietoFileException(filePath, e);
            }
            lineNumberCounter = new AtomicInteger(0);
        }

        List<MuutostietoDto> muutostiedot;
        try (Stream<MuutostietoLine> muutostietoLines = this.fileService.processFile(path, (line, lineNumber) ->
                        new MuutostietoLine(lineNumber, line))) {
            muutostiedot = muutostietoLines
                    .filter(line -> !line.content.startsWith("'''")) // Ignore metadata
                    .map(muutostietoParseService::deserializeMuutostietoLine)
                    .collect(Collectors.toList());
        } catch (MuutostietoLineParseException e) {
            throw new MuutostietoFileException(filePath, e);
        }
        squashMultipartMuutostiedot(muutostiedot).stream()
                .filter(muutostieto -> MuutosType.UUSI.equals(muutostieto.getMuutosType())
                        || MuutosType.VANHA.equals(muutostieto.getMuutosType()))
                .forEach(muutostieto -> {
                    muutostieto.setTiedostoNimi(fileName);
                    muutostieto.setRivi(lineNumberCounter.incrementAndGet());
                });
        this.muutostietoHandleService.importUnprocessedMuutostiedotToDb(muutostiedot, fileName);

        try {
            this.fileService.deleteImportFile(path);
        } catch (IOException e) {
            log.error("Failed to remove file from path: {}", path, e);
        }
        // Allow deleting only if file has been completely imported to DB.
        String pathString = path.toString();
        this.tiedostoRepository.findByFileName(fileName).ifPresent(tiedosto -> {
            if (this.fileService.parsePartNumber(pathString).longValue() == tiedosto.getPartCount()) {
                this.fileService.deleteBixFile(fileName);
            }
        });

        return muutostiedot;
    }

    @NotifyOnError(NotifyOnError.NotifyType.UPDATE)
    @Transactional(readOnly = true)
    @Override
    public void updateMuutostietos() {
        try {
            List<String> unprocessedFileNames = this.henkiloMuutostietoRepository.findDistinctUnprocessedTiedostoFileName();
            log.info("There are currently {} unprocessed files", unprocessedFileNames.size());
            Optional<String> firstFileToProcess = unprocessedFileNames.stream()
                    .min(this.fileService.byFileExtension().thenComparing(this.fileService.bySequentalNumbering()));
            log.info("First file to process is {}", firstFileToProcess);

            firstFileToProcess.ifPresent(fileName ->
                    this.henkiloMuutostietoRepository
                            .findByTiedostoFileNameAndProcessTimestampIsNullOrderByRivi(fileName)
                            .forEach(id -> {
                                HenkiloMuutostietoRivi henkiloMuutostietoRivi = this.henkiloMuutostietoRepository.findById(id)
                                        .orElseThrow(() -> new IllegalStateException(String.format("Could not find HenkiloMuutostietoRivi by id %d", id)));
                                this.muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);
                            }));
        } catch (RuntimeException e) { // lokitetaan, jotta virhe ei huku mikäli notifikaatio epäonnistuu
            log.error("Failed to update muutostietos", e); // ihanaa sekakieltä! <3
            throw e;
        }
    }

    /**
     * If muutos is on multiple lines combine to single rivi
     * @param muutostiedot all muutostiedot in the file by original lines
     * @return muutostiedot combined on multipart cases
     */
    public static List<MuutostietoDto> squashMultipartMuutostiedot(List<MuutostietoDto> muutostiedot) {
        Iterator<MuutostietoDto> i = muutostiedot.iterator();
        MuutostietoDto previous = null;
        while (i.hasNext()) {
            MuutostietoDto current = i.next();
            if (current.getMuutosType().equals(MuutosType.JATKETTU) && previous != null) {
                previous.getTietoryhmat().addAll(current.getTietoryhmat());
                i.remove();
            }
            else {
                previous = current;
            }
        }
        return muutostiedot;
    }

    @NotifyOnError(NotifyOnError.NotifyType.DOWNLOAD)
    @Override
    public List<String> downloadFiles() throws IOException {
        return fileService.downloadBixFiles();
    }


}
