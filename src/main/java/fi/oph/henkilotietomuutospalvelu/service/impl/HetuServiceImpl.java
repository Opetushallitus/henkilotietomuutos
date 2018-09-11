package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.annotations.NotifyOnError;
import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import fi.oph.henkilotietomuutospalvelu.model.type.VtjEventType;
import fi.oph.henkilotietomuutospalvelu.repository.VtjDataRepository;
import fi.oph.henkilotietomuutospalvelu.service.FileService;
import fi.oph.henkilotietomuutospalvelu.service.HetuService;
import lombok.RequiredArgsConstructor;
import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class HetuServiceImpl implements HetuService {

    private static final String CUSTOMER_NO = "38950";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final FileService fileService;

    private final VtjDataRepository vtjDataRepository;

    private enum HetuType {
        ADD("L"), REMOVE("P");

        private final String code;

        private static final Map<String, HetuType> map =
                Arrays.stream(HetuType.values()).collect(Collectors.toMap(type -> type.code, type -> type));

        HetuType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static HetuType getEnum(String code) {
            return map.get(code);
        }

    }

    @Override
    @Transactional
    public void updateHetusToDb(HetuDto hetuDto) {
        List<VtjDataEvent> vtjDataList = Stream.concat(hetuDto.getAddedHetus().stream()
                        .map(hetu -> this.mapHetuToVtjData(hetu, VtjEventType.ADD)),
                hetuDto.getRemovedHetus().stream()
                        .map(hetu -> this.mapHetuToVtjData(hetu, VtjEventType.REMOVE)))
                .collect(Collectors.toList());
        this.vtjDataRepository.saveAll(vtjDataList);
    }

    private VtjDataEvent mapHetuToVtjData(String hetu, VtjEventType vtjEventType) {
        return VtjDataEvent.builder()
                .hetu(hetu)
                .type(vtjEventType)
                .vtjdataTimestamp(null)
                .build();
    }

    @NotifyOnError(NotifyOnError.NotifyType.UPDATEHETU)
    @Override
    @Transactional
    public Set<String> updateHetusToVtj() {
        List<VtjDataEvent> vtjDataListToUpdate = this.vtjDataRepository.findByVtjdataTimestampIsNull();
        File file = createHenkilotunnusFile(vtjDataListToUpdate, LocalDateTime.now());
        try {
            this.fileService.uploadFileToBix(file);
        } catch (IOException ioe) {
            throw new RuntimeException("Could not add/remove hetus to vtj", ioe);
        }
        vtjDataListToUpdate.forEach(unupdatedVtjData -> unupdatedVtjData.setVtjdataTimestamp(LocalDateTime.now()));
        return vtjDataListToUpdate.stream().map(VtjDataEvent::getHetu).collect(Collectors.toSet());
    }

    public static File createHenkilotunnusFile(List<VtjDataEvent> vtjDataEventList, LocalDateTime dateTime) {
        List<String> addedHetus = vtjDataEventList.stream()
                .filter(vtjDataEvent -> VtjEventType.ADD.equals(vtjDataEvent.getType()))
                .map(VtjDataEvent::getHetu)
                .collect(Collectors.toList());
        List<String> removedHetus = vtjDataEventList.stream()
                .filter(vtjDataEvent -> VtjEventType.REMOVE.equals(vtjDataEvent.getType()))
                .map(VtjDataEvent::getHetu)
                .collect(Collectors.toList());

        String fileContent = String.valueOf(header(dateTime)) +
                content(addedHetus, HetuType.ADD) +
                content(removedHetus, HetuType.REMOVE) +
                footer(addedHetus.size() + removedHetus.size());

        File file = new File(getTmpDir() + "/" + getHetuFilename(dateTime));
        try {
            FileUtils.write(file, fileContent, "UTF-8");
        } catch (IOException e) {
            log.error("Could not write content to the file!", e);
        }
        return file;
    }

    private static StringBuilder header(LocalDateTime dateTime) {
        return new StringBuilder()
                .append("\'\'\'ED2\'\'\'VRK:003702454372:OVT\'TIETO:0037OPHALL:OVT\'")
                .append(dateTime.format(DateTimeFormatter.ofPattern("YYYYMMddHHmm")))
                .append("\'1\'STX:INHOUSE\'TYP:MUTPHT\'NRQ:0\'\'\'")
                .append(LINE_SEPARATOR);
    }

    private static StringBuilder content(List<String> hetus, HetuType type) {
        StringBuilder builder = new StringBuilder();
        for (String hetu : hetus) {
            builder.append(hetu)
                    .append(type.getCode())
                    .append(LINE_SEPARATOR);
        }
        return builder;
    }

    private static StringBuilder footer(int lines) {
        return new StringBuilder()
                .append("\'\'\'EOF\'\'\'")
                .append(lines)
                .append("\'")
                .append(LINE_SEPARATOR);
    }

    private static String getTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

    private static String getHetuFilename(LocalDateTime dateTime) {
        return getFileName("_", "YYYYMMdd", ".HTT", dateTime);
    }

    private static String getFileName(String prefix, String dateFormat, String suffix) {
        return getFileName(prefix, dateFormat, suffix, LocalDateTime.now());
    }

    private static String getFileName(String prefix, String dateFormat, String suffix, LocalDateTime time) {
        return CUSTOMER_NO + prefix + time.format(DateTimeFormatter.ofPattern(dateFormat)) + suffix;
    }

}
