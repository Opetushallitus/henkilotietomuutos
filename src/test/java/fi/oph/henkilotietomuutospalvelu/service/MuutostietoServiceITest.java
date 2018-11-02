package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.IntegrationTest;
import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.properties.AWSProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.vm.sade.oppijanumerorekisteri.dto.*;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@IntegrationTest
// This needs to be done manually instead of @Transactional because MuutostietoHandleService uses REQUIRES_NEW propagation
// that doesn't sit well with @Transactional (TiedostoRepository doesn't return anything in new transaction)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/sql/muutostietoServiceData.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = {"DELETE tietoryhma;", "DELETE henkilo_muutostieto_rivi;", "DELETE tiedosto;"})
})
public class MuutostietoServiceITest {

    @Autowired
    private MuutostietoService muutostietoService;

    @Autowired
    private MuutostietoHandleService muutostietoHandleService;

    @SpyBean
    private FileService fileService;

    @MockBean
    private FtpProperties ftpProperties;

    @MockBean
    private AWSProperties awsProperties;

    @MockBean
    private OnrServiceClient onrServiceClient;

    @Autowired
    private HenkiloMuutostietoRepository henkiloMuutostietoRepository;

    @Autowired
    private MuutostietoParseService muutostietoParseService;

    @MockBean
    private KoodistoService koodistoService;

    @Captor
    private ArgumentCaptor<HenkiloForceUpdateDto> captor;

    @Before
    public void setup() throws IOException {
        Mockito.doNothing().when(fileService).deleteImportFile(any());
        Mockito.doNothing().when(fileService).deleteImportFile(any());
    }

    private void mockFiles(String nextFileName, String splitFileName) throws URISyntaxException, IOException {
        Path nextFile = Paths.get(ClassLoader.getSystemResource(nextFileName).toURI());
        doReturn(Optional.of(nextFile.toString())).when(fileService).findNextFile();
        Path splitFile = Paths.get(ClassLoader.getSystemResource(splitFileName).toURI());
        doReturn(splitFile).when(fileService).splitFile(any());
    }

    @Test
    public void whenUtf8FileIsReadGenerateProperUpdateDto() throws Exception {
        mockFiles("test_data/aakkoset_test.PTT", "test_data/aakkoset_test.PTT_001.PART");

        given(this.koodistoService.list(eq(Koodisto.MAAT_JA_VALTIOT_2)))
                .willReturn(Arrays.asList(KoodiDto.builder().koodiArvo("246").build(),
                        KoodiDto.builder().koodiArvo("512").build()));
        given(this.koodistoService.list(eq(Koodisto.HUOLTAJUUSTYYPPI)))
                .willReturn(Collections.singletonList(KoodiDto.builder().koodiArvo("03").build()));

        HenkiloDto henkilo = new HenkiloDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setPassivoitu(false);
        Mockito.when(onrServiceClient.getHenkiloByHetu("100271-008M")).thenReturn(Optional.of(henkilo));

        this.muutostietoService.importMuutostiedot(0);
        this.muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updatedHenkilo = captor.getValue();
        assertThat(updatedHenkilo)
                .extracting(HenkiloUpdateDto::getOidHenkilo,
                        HenkiloUpdateDto::getEtunimet,
                        HenkiloUpdateDto::getSukunimi,
                        HenkiloUpdateDto::getSukupuoli)
                .containsExactly("1.2.246.562.24.41327169638",
                        "Tarja Annika",
                        "Pälömäki Täs",
                        "2");
        assertThat(updatedHenkilo.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactlyInAnyOrder("140434-0665", "");
        assertThat(filter(updatedHenkilo.getHuoltajat()).with("hetu", "").get())
                .extracting(HuoltajaCreateDto::getEtunimet,
                        HuoltajaCreateDto::getSukunimi,
                        HuoltajaCreateDto::getKansalaisuusKoodi,
                        HuoltajaCreateDto::getHuoltajuustyyppiKoodi,
                        HuoltajaCreateDto::getSyntymaaika)
                .containsExactly(Tuple.tuple("Testi Test",
                        "Testinen",
                        Collections.singleton("246"),
                        "03",
                        null));
    }

    @Test
    public void whenPersonHasNimenmuutosWithExpirationDate_thenIgnoreNimenmuutos() throws Exception {
        mockFiles("test_data/nimi_test.PTT", "test_data/nimi_test.PTT_001.PART");

        HenkiloDto henkilo = new HenkiloDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setPassivoitu(false);
        Mockito.when(onrServiceClient.getHenkiloByHetu("030552-085W")).thenReturn(Optional.of(henkilo));

        this.muutostietoService.importMuutostiedot(0);
        this.muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updatedHenkilo = captor.getValue();
        assertThat(updatedHenkilo)
                .extracting(HenkiloUpdateDto::getOidHenkilo,
                        HenkiloUpdateDto::getEtunimet,
                        HenkiloUpdateDto::getSukunimi,
                        HenkiloUpdateDto::getSukupuoli)
                .containsExactly("1.2.246.562.24.41327169638",
                        "Matti",
                        "Köskinen",
                        "2");
        assertThat(updatedHenkilo.getHuoltajat()).isEmpty();
    }

    @Test
    public void henkiloAidinkieliKansalaisuusTurvakieltoChange() throws Exception {
        mockFiles("test_data/nimi_test.PTT", "test_data/nimi_test.PTT_001.PART");
        given(this.koodistoService.list(eq(Koodisto.KIELI))).willReturn(Collections.singletonList(KoodiDto.builder()
                .koodiArvo("fi")
                .build()));
        given(this.koodistoService.list(eq(Koodisto.MAAT_JA_VALTIOT_2)))
                .willReturn(Arrays.asList(KoodiDto.builder().koodiArvo("246").build(),
                        KoodiDto.builder().koodiArvo("512").build()));

        HenkiloDto henkilo = new HenkiloDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setPassivoitu(false);
        henkilo.setTurvakielto(false);
        henkilo.setYhteystiedotRyhma(Stream.of(
                YhteystiedotRyhmaDto.builder().ryhmaAlkuperaTieto("alkupera1").ryhmaKuvaus("yhteystietotyyppi4").build(),
                YhteystiedotRyhmaDto.builder().ryhmaAlkuperaTieto("alkupera1").ryhmaKuvaus("yhteystietotyyppi5").build(),
                YhteystiedotRyhmaDto.builder().ryhmaAlkuperaTieto("alkupera2").ryhmaKuvaus("yhteystietotyyppi2").build()
        ).collect(toSet()));
        Mockito.when(onrServiceClient.getHenkiloByHetu("030552-085W")).thenReturn(Optional.of(henkilo));

        this.muutostietoService.importMuutostiedot(0);
        this.muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updatedHenkilo = captor.getValue();
        assertThat(updatedHenkilo)
                .extracting(HenkiloForceUpdateDto::getOidHenkilo,
                        henkiloForceUpdateDto -> henkiloForceUpdateDto.getAidinkieli().getKieliKoodi(),
                        HenkiloForceUpdateDto::getTurvakielto)
                .containsExactly("1.2.246.562.24.41327169638", "fi", true);
        // Kaksoiskansalaisuus
        assertThat(updatedHenkilo.getKansalaisuus())
                .flatExtracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactlyInAnyOrder("246", "512");
        assertThat(updatedHenkilo.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaAlkuperaTieto, YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactly(tuple("alkupera2", "yhteystietotyyppi2"));
        assertThat(updatedHenkilo.getHuoltajat()).isEmpty();
    }

    @Test
    public void henkiloHetuAndSukupuoliAndNimiChanges() throws Exception {
        mockFiles("test_data/hetu_changes.MTT_001.PART", "test_data/hetu_changes.MTT_001.PART");

        HenkiloDto henkilo = new HenkiloDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setPassivoitu(false);
        henkilo.setHetu("250198-9606");
        henkilo.setSukupuoli("2");
        henkilo.setEtunimet("Vanhaetunimi");
        henkilo.setSukunimi("Vanhasukunimi");
        Mockito.when(this.onrServiceClient.getHenkiloByHetu("250198-9606")).thenReturn(Optional.of(henkilo));

        List<MuutostietoDto> muutostietoDtos = this.muutostietoService.importMuutostiedot(0);
        this.muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updatedHenkilo = captor.getValue();

        assertThat(muutostietoDtos)
                .hasSize(1)
                .flatExtracting(MuutostietoDto::getTietoryhmat)
                .hasSize(5)
                .extracting(Tietoryhma::getMuutostapa, Tietoryhma::getRyhmatunnus)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(Muutostapa.LISATTY, Ryhmatunnus.HENKILOTUNNUS_KORJAUS),
                        Tuple.tuple(Muutostapa.MUUTETTU, Ryhmatunnus.HENKILOTUNNUS_KORJAUS),
                        Tuple.tuple(Muutostapa.MUUTETTU, Ryhmatunnus.SUKUPUOLI),
                        Tuple.tuple(Muutostapa.LISATIETO, Ryhmatunnus.HENKILO_NIMI),
                        Tuple.tuple(Muutostapa.LISATIETO, Ryhmatunnus.KUTSUMANIMI));

        assertThat(updatedHenkilo.getSukupuoli()).isEqualTo("1");
        assertThat(updatedHenkilo.getHetu()).isEqualTo("250198-9019");
        // Muutostapa.LISATIETO won't be updated
        assertThat(updatedHenkilo.getEtunimet()).isNull();
        assertThat(updatedHenkilo.getSukunimi()).isNull();
        assertThat(updatedHenkilo.getHuoltajat()).isEmpty();
    }
}
