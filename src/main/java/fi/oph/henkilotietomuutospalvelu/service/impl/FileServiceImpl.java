package fi.oph.henkilotietomuutospalvelu.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.collect.Lists;
import fi.oph.henkilotietomuutospalvelu.client.BIXClient;
import fi.oph.henkilotietomuutospalvelu.config.properties.AWSProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FtpProperties ftpProperties;
    private final AWSProperties awsProperties;

    private final TiedostoRepository tiedostoRepository;

    private static final List<String> EXTENSIONS = Arrays.asList("MTT", "PTT", "PART");
    private static final long LINE_LIMIT = 50000;

    @Override
    public List<String> downloadBixFiles() throws IOException {
        try (BIXClient bixClient = new BIXClient(ftpProperties)) {
            List<String> fileNames = bixClient.getFiles();

            final String downloadDir = ftpProperties.getLocalDownloadDir();
            List<Path> filePaths = fileNames.stream().map(filename -> Paths.get(downloadDir + filename)).collect(Collectors.toList());

            verifyPaths(filePaths);
            copyFilesToBackup(filePaths);

            moveFilesToImportDir(filePaths);

            return fileNames;
        }
    }

    @Override
    public void deleteBixFile(String fileName) {
        if (ftpProperties.isdeleteModeOn()) {
            try (BIXClient bixClient = new BIXClient(ftpProperties)) {
                bixClient.deleteFiles(Lists.newArrayList(fileName));
            } catch (IOException e) {
                log.error("Could not delete SFTP file", e);
            }
        }
        else {
            log.info("File deletion is turned off. File {} was not deleted from SFTP-server.", fileName);
        }
    }

    @Override
    public void uploadFileToBix(File file) throws IOException {
        try (BIXClient bixClient = new BIXClient(ftpProperties)) {
            bixClient.uploadFile(file);
        }
    }

    @Override
    public Optional<String> findNextFile() throws IOException {
        return findNextFile(ftpProperties.getLocalImportDir());
    }

    @Override
    public List<String> readFile(Path path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8))) {
            log.debug("Reading file from path: {}", path);
            return br.lines().collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to read file from path: {}", path);
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteImportFile(Path path) throws IOException {
        Files.delete(path);
    }

    /**
     * Files have to be imported in the correct order. This function will find the next one
     * based on their extension and sequential number.
     * @param directory import files directory
     * @return Optional path to the next file
     * @throws IOException Something fails on file handling
     */
    @Override
    public Optional<String> findNextFile(final String directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(p -> EXTENSIONS.contains(FilenameUtils.getExtension(p)))
                    .peek(p -> log.debug("Found file {}", p))
                    .min(this.byFileExtension().thenComparing(this.bySequentalNumbering()));
        }
    }

    /**
     * Almost every rivi in the file is independent of each other excluding continuation lines.
     * Continuation lines continue previous lines and the file cannot be split between them.
     *
     * Encode PART files to UTF-8.
     * @param path Path to the file to be split
     * @return Path to the first part of the file
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Path splitFile(Path path) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.ISO_8859_1))) {
            int part = 1;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format("%s_%03d.PART", path, part)), StandardCharsets.UTF_8));

            String line;
            int linesRead = 0;
            while ((line = br.readLine()) != null) {
                if (linesRead >= LINE_LIMIT) {
                    if (line.charAt(23) != 'J') {
                        bw.close();
                        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format("%s_%03d.PART", path, ++part)), StandardCharsets.UTF_8));
                        linesRead = 0;
                    }
                }
                bw.write(line + "\n");
                linesRead++;
            }
            bw.close();

            // No need to create duplicate if file has parts have already been counted. (Should always have same result)
            String fileName = FilenameUtils.getName(path.toString());
            if (!this.tiedostoRepository.findByFileName(fileName).isPresent()) {
                this.tiedostoRepository.save(new Tiedosto(fileName, part));
            }
        }

        Files.delete(path);
        return Paths.get(String.format("%s_001.PART", path));
    }

    /**
     * Gets original filename of .PART file
     * @param path path to .PART file
     * @return Original file name
     */
    public static String getOriginalNameFromPath(String path) {
        String partFileName = FilenameUtils.getName(path);
        return partFileName.substring(0, partFileName.lastIndexOf("_"));
    }

    private void verifyPaths(List<Path> filePaths) throws IOException {
        for (Path path : filePaths) {
            if (path == null || !Files.exists(path)) {
                throw new IOException("File doesn't exist, something must have gone wrong with the transfer.");
            } else if (!Files.isReadable(path)) {
                throw new IOException(String.format("Could not verify file %s, something must have gone wrong with the transfer.", path.toString()));
            }
        }
    }

    private void copyFilesToBackup(List<Path> filePaths) throws IOException {
        if (awsProperties.isS3InUse()) {
            copyFilesToAWS(filePaths);
        } else {
            copyFilesToLongTermStorage(filePaths);
        }
    }

    private void moveFilesToImportDir(List<Path> filePaths) throws IOException {
        for (Path filePath : filePaths) {
            Path importPath = Paths.get(ftpProperties.getLocalImportDir() + filePath.getFileName());
            Files.move(filePath, importPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void copyFilesToAWS(List<Path> filePaths) throws IOException {
        AmazonS3 s3Client = null;
        InstanceProfileCredentialsProvider credProvider = null;
        try {
            credProvider = InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(true);
            s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(credProvider)
                .withRegion(awsProperties.getRegion())
                .build();
            String bucketName = awsProperties.getBucket();

            for (Path path : filePaths) {
                s3Client.putObject(new PutObjectRequest(bucketName, path.getFileName().toString(), path.toFile()));
            }
        } catch (AmazonServiceException e) {
            throw new IOException(String.format("Failed to copy files to S3. Region: %s, Bucket: %s", awsProperties.getRegion(), awsProperties.getBucket()), e);
        } finally {
            if (s3Client != null) {
                s3Client.shutdown();
            }
            if (credProvider != null) {
                credProvider.close();
            }
        }
    }

    private void copyFilesToLongTermStorage(List<Path> filePaths) throws IOException {
        for (Path path : filePaths) {
            Path backupPath = Paths.get(ftpProperties.getLocalBackupDir() + path.getFileName());
            Files.copy(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public Comparator<String> bySequentalNumbering() {
        return (String left, String right) -> {
            Long l1 = parsePartNumber(left);
            Long l2 = parsePartNumber(right);
            return l1.compareTo(l2);
        };
    }

    @Override
    // Compare extensions and prioritize .PTT files over .MTT files
    public Comparator<String> byFileExtension() {
        return (String left, String right) -> {
            String e1 = FilenameUtils.getExtension(left);
            String e2 = FilenameUtils.getExtension(right);

            if (e1.equals(e2)) {
                return 0;
            } else if (e1.equals("PART")) {
                return -1;
            } else if (e1.equals("PTT") && e2.equals("MTT")) {
                return -1;
            } else {
                return 1;
            }
        };
    }

    /**
     * PTT and MTT files are saved using the following filenames.
     * [customer number]_[date string]OPHREK_[sequential number].[file extension]
     * PTT => 38950_PT171024OPHREK_001.PTT
     * MTT => 38950_20171024OPHREK_004.MTT
     * @param path partial file path
     * @return value of the sequential number
     */
    @Override
    public Long parsePartNumber(String path) {
        try {
            String numberAndExtension = path.substring(path.lastIndexOf("_") + 1);
            String number = numberAndExtension.substring(0, numberAndExtension.indexOf("."));
            return Long.valueOf(number);
        } catch (StringIndexOutOfBoundsException e) {
            return 0L;
        }
    }
}
