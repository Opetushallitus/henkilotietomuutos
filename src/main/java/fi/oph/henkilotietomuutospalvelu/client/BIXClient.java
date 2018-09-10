package fi.oph.henkilotietomuutospalvelu.client;

import com.jcraft.jsch.*;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class BIXClient implements Closeable {

    private ChannelSftp channel;
    private FtpProperties ftpProperties;

    public BIXClient(FtpProperties ftpProperties) throws IOException {
        this.ftpProperties = ftpProperties;
        initializeSession();
    }

    private static class UserInfoDebugger implements UserInfo {

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassword(String message) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return false;
        }

        @Override
        public boolean promptYesNo(String message) {
            return false;
        }

        @Override
        public void showMessage(String message) {
            log.info(message);
        }
    }

    private static final Logger LOGWRAP=new Logger(){
        public boolean isEnabled(int level) {
            return true;
        }
        public void log(int level, String message) {
            if(DEBUG == level) {
                log.debug(message);
            } else if(INFO == level) {
                log.info(message);
            } else if(WARN == level) {
                log.warn(message);
            } else if(ERROR == level) {
                log.error(message);
            } else if(FATAL == level) {
                log.error(message);
            }
        }
    };

    private void initializeSession() throws IOException {
        try {
            JSch jSch = new JSch();

            JSch.setLogger(LOGWRAP);

            HostKeyRepository repository = jSch.getHostKeyRepository();
            if (ftpProperties.isDevMode()) {
                repository.add(getHostKey("localhost"), null); // Enable ssh tunneling
            } else {
                repository.add(getHostKey(ftpProperties.getHost()), new UserInfoDebugger());
            }


            if(repository.getHostKey() != null) {
                log.info("using hostkeys:" + Arrays.stream(repository.getHostKey()).map(HostKey::getHost).collect(Collectors.joining(",")));
            }

            Session session = jSch.getSession(ftpProperties.getUser(), ftpProperties.getHost(), Integer.valueOf(ftpProperties.getPort()));
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setPassword(ftpProperties.getPassword());
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            throw new IOException("Could not open sftp session.", e);
        }
    }

    public List<String> getFiles() throws IOException {
        String localDownloadDir = ftpProperties.getLocalDownloadDir();
        String downloadDir = ftpProperties.getDownloadDir();

        List<String> filepaths = listFiles(downloadDir);
        for (String file : filepaths) {
            getFile(downloadDir + file, localDownloadDir + file);
        }
        return filepaths;
    }

    public void uploadFile(File file) throws IOException {
        putFile(file.getAbsolutePath(), ftpProperties.getUploadDir() + file.getName());
    }

    public void deleteFiles(List<String> filePaths) throws IOException {
        for (String path : filePaths) {
            deleteFile(ftpProperties.getDownloadDir() + path);
        }
    }

    private List<String> listFiles(final String sourceDir) throws IOException {
        try {
            Vector<ChannelSftp.LsEntry> fileEntries = channel.ls(sourceDir);
            return fileEntries.stream()
                    .map(ChannelSftp.LsEntry::getFilename)
                    .filter(e -> e.endsWith(".MTT") || e.endsWith(".PTT"))
                    .collect(Collectors.toList());
        } catch(SftpException e) {
            throw new IOException("Could not list directory contents.", e);
        }
    }

    private void getFile(String source, String destination) throws IOException {
        try {
            channel.get(source, destination);
        } catch (SftpException e) {
            throw new IOException(String.format("Could not get file from %s to %s", source, destination), e);
        }
    }

    private void putFile(String source, String destination) throws IOException {
        try {
            channel.put(source, destination);
        } catch (SftpException e) {
            throw new IOException(String.format("Could not put file from %s to %s", source, destination), e);
        }
    }

    private void deleteFile(String path) throws IOException {
        try {
            channel.rm(path);
        } catch (SftpException e ) {
            throw new IOException(String.format("Could not delete file: %s", path), e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            Session session = channel.getSession();
            channel.exit();
            session.disconnect();
        } catch (JSchException e) {
            throw new IOException("Could not retrieve session from channel.", e);
        }
    }

    private HostKey getHostKey(String host) throws JSchException {
        byte[] key = Base64.getDecoder().decode(ftpProperties.getHostKey());
        return new HostKey(host, HostKey.SSHRSA, key);
    }

}
