package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.config.properties.AWSProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import fi.oph.henkilotietomuutospalvelu.model.type.VtjEventType;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.service.impl.FileServiceImpl;
import fi.oph.henkilotietomuutospalvelu.service.impl.HetuServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
public class FileServiceTest {
    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private FtpProperties ftpProperties;

    @Mock
    private AWSProperties awsProperties;

    @Mock
    private TiedostoRepository tiedostoRepository;

    @Test
    public void parseNumberFromValidFilename() {
        Path path = Paths.get("/home/example/38950_20171027OPHREK_011.MTT");
        Long number = this.fileService.parsePartNumber(path.toString());
        Assert.assertEquals(new Long(11), number);
    }

    @Test
    public void parseNumberFromInvalidFilename() {
        Path path = Paths.get("test_file");
        Long number = this.fileService.parsePartNumber(path.toString());
        Assert.assertEquals(new Long(0), number);
    }

    @Test
    public void sortFilePaths() {
        List<String> unsortedPaths = Arrays.asList(
            "38950_PT171030OPHREK_002.PTT",
            "38950_20171024OPHREK_001.MTT",
            "38950_20171025OPHREK_002.MTT",
            "38950_20171026OPHREK_003.MTT",
            "38950_20171027OPHREK_006.MTT",
            "38950_20171028OPHREK_004.MTT",
            "38950_20171029OPHREK_005.MTT",
            "38950_20171024OPHREK_001.PTT_001.PART",
            "38950_20171024OPHREK_001.PTT_002.PART",
            "38950_20171024OPHREK_001.PTT_003.PART",
            "38950_20171024OPHREK_001.PTT_004.PART",
            "38950_PT171024OPHREK_001.PTT",
            "38950_20171030OPHREK_007.MTT"
        );

        List<String> correctPaths = Arrays.asList(
            "38950_20171024OPHREK_001.PTT_001.PART",
            "38950_20171024OPHREK_001.PTT_002.PART",
            "38950_20171024OPHREK_001.PTT_003.PART",
            "38950_20171024OPHREK_001.PTT_004.PART",
            "38950_PT171024OPHREK_001.PTT",
            "38950_PT171030OPHREK_002.PTT",
            "38950_20171024OPHREK_001.MTT",
            "38950_20171025OPHREK_002.MTT",
            "38950_20171026OPHREK_003.MTT",
            "38950_20171028OPHREK_004.MTT",
            "38950_20171029OPHREK_005.MTT",
            "38950_20171027OPHREK_006.MTT",
            "38950_20171030OPHREK_007.MTT"

        );

        List<String> sortedPaths = unsortedPaths.stream()
                .sorted(this.fileService.byFileExtension().thenComparing(this.fileService.bySequentalNumbering()))
                .collect(Collectors.toList());

        assertThat(sortedPaths).isEqualTo(correctPaths);
    }

    @Test
    public void createHenkilotunnusFile() {
        final List<String> addedHetus = Arrays.asList("010141-412A", "030330-133C",
                                                "010183-792D", "010141-481J",
                                                "010141-489T", "241150-6518",
                                                "150755-8929", "010141-506B",
                                                "010141-516N", "010141-5260",
                                                "060371-807M", "010141-443A");
        final List<String> removedHetus = Arrays.asList("010141-516N", "010141-5260", "060371-807M");

        List<VtjDataEvent> vtjDataEvents = new ArrayList<>();
        vtjDataEvents.addAll(addedHetus.stream().map(hetu -> VtjDataEvent.builder().hetu(hetu).type(VtjEventType.ADD).build()).collect(Collectors.toList()));
        vtjDataEvents.addAll(removedHetus.stream().map(hetu -> VtjDataEvent.builder().hetu(hetu).type(VtjEventType.REMOVE).build()).collect(Collectors.toList()));

        URL url = this.getClass().getResource("/test_data/38950_20171116.HTT");
        File expectedFile = new File(url.getFile());

        File hetuFile = HetuServiceImpl.createHenkilotunnusFile(vtjDataEvents, LocalDateTime.of(2017, 11, 16, 13, 27));

        assertThat(hetuFile.getName()).isEqualTo("38950_20171116.HTT").as("Filenames are not equal!");
        assertThat(hetuFile).hasSameContentAs(expectedFile).as("File contents are not equal!");

    }

}
