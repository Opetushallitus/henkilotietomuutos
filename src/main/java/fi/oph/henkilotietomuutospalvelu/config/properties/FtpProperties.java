package fi.oph.henkilotietomuutospalvelu.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "clients.ftp")
public class FtpProperties {

    @NotBlank
    private String user;
    @NotBlank
    private String password;
    @NotBlank
    private String hostKey;

    private String host;
    private String port;
    private String downloadDir = "/download/test";
    private String uploadDir = "/upload/test";

    @NotBlank
    private String localDownloadDir;
    @NotBlank
    private String localImportDir;
    @NotBlank
    private String localBackupDir;

    @Getter(AccessLevel.NONE)
    private boolean deleteFiles;
    private boolean devMode;

    public Boolean isdeleteModeOn() {
        return deleteFiles;
    }

    @PostConstruct
    public void initialize() {
        localDownloadDir = createDirectories(localDownloadDir);
        localImportDir = createDirectories(localImportDir);
        localBackupDir = createDirectories(localBackupDir);
    }

    private static String createDirectories(String path) {
        try {
            File file = new File(path);
            if ((file.exists() && file.isDirectory()) || file.mkdirs()) {
                return file.getCanonicalPath() + "/";
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Directory %s does not exists and cannot be created!", path));
        }
    }
}
