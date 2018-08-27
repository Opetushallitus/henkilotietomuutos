package fi.oph.henkilotietomuutospalvelu.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FileService {
    List<String> downloadBixFiles() throws IOException;

    void deleteBixFile(String fileName);

    void uploadFileToBix(File file) throws IOException;

    Optional<String> findNextFile() throws IOException;

    /**
     * Reads file content
     * @param path file path
     * @return file content as lines in ordered stream
     */
    List<String> readFile(Path path);

    void deleteImportFile(Path path) throws IOException;

    Optional<String> findNextFile(String directory) throws IOException;

    Path splitFile(Path path) throws IOException;

    Comparator<String> bySequentalNumbering();

    // Compare extensions and prioritize .PTT files over .MTT files
    Comparator<String> byFileExtension();

    Long parsePartNumber(String path);
}
