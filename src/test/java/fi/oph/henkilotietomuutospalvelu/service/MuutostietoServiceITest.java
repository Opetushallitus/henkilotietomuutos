package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.IntegrationTest;
import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.client.VtjServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.properties.AWSProperties;
import fi.oph.henkilotietomuutospalvelu.config.properties.FtpProperties;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.HuoltajaRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.service.exception.MuutostietoFileException;
import fi.vm.sade.oppijanumerorekisteri.dto.*;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@IntegrationTest
// This needs to be done manually instead of @Transactional because MuutostietoHandleService uses REQUIRES_NEW propagation
// that doesn't sit well with @Transactional (TiedostoRepository doesn't return anything in new transaction)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/sql/muutostietoServiceData.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = {"DELETE huoltaja_oikeudet;", "DELETE tietoryhma;", "DELETE henkilo_muutostieto_rivi;", "DELETE tiedosto;"})
})
public class MuutostietoServiceITest {

    @Autowired
    private MuutostietoService muutostietoService;

    @Autowired
    private MuutostietoHandleService muutostietoHandleService;

    @Autowired
    private TiedostoRepository tiedostoRepository;

    @Autowired
    private HuoltajaRepository huoltajaRepository;

    @Autowired
    private DatabaseService databaseService;

    @SpyBean
    private FileService fileService;

    @SpyBean
    private TimeService timeService;

    @MockBean
    private FtpProperties ftpProperties;

    @MockBean
    private AWSProperties awsProperties;

    @MockBean
    private OnrServiceClient onrServiceClient;

    @MockBean
    private VtjServiceClient vtjServiceClient;

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

        given(this.koodistoService.isKoodiValid(eq(Koodisto.MAAT_JA_VALTIOT_2), eq("246"))).willReturn(true);
        given(this.koodistoService.isKoodiValid(eq(Koodisto.MAAT_JA_VALTIOT_2), eq("512"))).willReturn(true);

        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setHetu("140434-0665");
        given(this.vtjServiceClient.getHenkiloByHetu("140434-0665")).willReturn(Optional.of(yksiloityHenkilo));

        HenkiloForceReadDto henkilo = new HenkiloForceReadDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setHetu("100271-008M");
        henkilo.setPassivoitu(false);
        when(onrServiceClient.getHenkiloByHetu("100271-008M")).thenReturn(Optional.of(henkilo));

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
                .containsExactlyInAnyOrder("140434-0665", null);
        assertThat(filter(updatedHenkilo.getHuoltajat()).with("hetu", null).get())
                .extracting(HuoltajaCreateDto::getEtunimet,
                        HuoltajaCreateDto::getSukunimi,
                        HuoltajaCreateDto::getKansalaisuusKoodi,
                        HuoltajaCreateDto::getSyntymaaika)
                .containsExactly(Tuple.tuple("Testi Test",
                        "Testinen",
                        Collections.singleton("246"),
                        null));
    }

    @Test
    public void whenPersonHasNimenmuutosWithExpirationDate_thenIgnoreNimenmuutos() throws Exception {
        mockFiles("test_data/nimi_test.PTT", "test_data/nimi_test.PTT_001.PART");

        HenkiloForceReadDto henkilo = new HenkiloForceReadDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setHetu("030552-085W");
        henkilo.setPassivoitu(false);
        when(onrServiceClient.getHenkiloByHetu("030552-085W")).thenReturn(Optional.of(henkilo));

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
    public void turvakieltoOletusarvoFalsePerustietoaineisto() {
        String tiedostonimi = "test_001.PTT";
        String hetu = "281198-911L";
        tallennaTiedosto(tiedostonimi, MuutostietoDto.builder()
                .tiedostoNimi(tiedostonimi)
                .hetu(hetu)
                .tietoryhmat(emptyList())
                .build());
        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu))).thenReturn(Optional.of(readDto));

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = captor.getValue();
        assertThat(updateDto).returns(false, HenkiloForceUpdateDto::getTurvakielto);
    }

    @Test
    public void turvakieltoOletusarvoNullMuutostietoaineisto() {
        String tiedostonimi = "test_001.MTT";
        String hetu = "281198-911L";
        tallennaTiedosto(tiedostonimi, MuutostietoDto.builder()
                .tiedostoNimi(tiedostonimi)
                .hetu(hetu)
                .tietoryhmat(emptyList())
                .build());
        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu))).thenReturn(Optional.of(readDto));

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = captor.getValue();
        assertThat(updateDto).returns(null, HenkiloForceUpdateDto::getTurvakielto);
    }

    @Test
    public void henkiloAidinkieliKansalaisuusTurvakieltoChange() throws Exception {
        mockFiles("test_data/nimi_test.PTT", "test_data/nimi_test.PTT_001.PART");
        given(this.koodistoService.isKoodiValid(eq(Koodisto.KIELI), eq("fi"))).willReturn(true);
        given(this.koodistoService.isKoodiValid(eq(Koodisto.MAAT_JA_VALTIOT_2), eq("512"))).willReturn(true);
        given(this.koodistoService.isKoodiValid(eq(Koodisto.MAAT_JA_VALTIOT_2), eq("246"))).willReturn(true);

        HenkiloForceReadDto henkilo = new HenkiloForceReadDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setHetu("030552-085W");
        henkilo.setPassivoitu(false);
        henkilo.setTurvakielto(false);
        henkilo.setYhteystiedotRyhma(Stream.of(
                YhteystiedotRyhmaDto.builder().ryhmaAlkuperaTieto("alkupera1").ryhmaKuvaus("yhteystietotyyppi4").build(),
                YhteystiedotRyhmaDto.builder().ryhmaAlkuperaTieto("alkupera1").ryhmaKuvaus("yhteystietotyyppi5").build(),
                YhteystiedotRyhmaDto.builder().ryhmaAlkuperaTieto("alkupera2").ryhmaKuvaus("yhteystietotyyppi2").build()
        ).collect(toSet()));
        when(onrServiceClient.getHenkiloByHetu("030552-085W")).thenReturn(Optional.of(henkilo));

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

        HenkiloForceReadDto henkilo = new HenkiloForceReadDto();
        henkilo.setOidHenkilo("1.2.246.562.24.41327169638");
        henkilo.setPassivoitu(false);
        henkilo.setHetu("250198-9606");
        henkilo.setSukupuoli("2");
        henkilo.setEtunimet("Vanhaetunimi");
        henkilo.setSukunimi("Vanhasukunimi");
        when(this.onrServiceClient.getHenkiloByHetu("250198-9606")).thenReturn(Optional.of(henkilo));

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

    @Test
    public void importMuutostiedotHuoltaja() throws IOException, URISyntaxException, MuutostietoFileException {
        mockFiles("test_data/huoltaja_test.PTT", "test_data/huoltaja_test.PTT_001.PART");

        muutostietoService.importMuutostiedot(0);

        databaseService.runInTransaction(() -> {
            assertThat(henkiloMuutostietoRepository.findAll()).hasSize(1);
            assertThat(huoltajaRepository.findByHetu("281199-9493")).isNotPresent();
            assertThat(huoltajaRepository.findByHetu("281179-903E")).hasValueSatisfying(huoltaja -> {
                assertThat(huoltaja)
                        .returns(Muutostapa.LISATTY, Huoltaja::getMuutostapa)
                        .returns("1", Huoltaja::getLaji)
                        .returns("1", Huoltaja::getRooli)
                        .returns(null, Huoltaja::getStartDate)
                        .returns(null, Huoltaja::getEndDate);
                assertThat(huoltaja.getOikeudet())
                        .extracting(Oikeus::getMuutostapa, Oikeus::getKoodi, Oikeus::getAlkupvm, Oikeus::getLoppupvm, Oikeus::getHenkiloMuutostietoRivi)
                        .containsExactlyInAnyOrder(
                                tuple(Muutostapa.LISATTY, "T301", null, LocalDate.of(2019, 11, 28), null),
                                tuple(Muutostapa.LISATTY, "T302", LocalDate.of(2000, 2, 2), LocalDate.of(2005, 5, 5), null));
            });
            assertThat(huoltajaRepository.findByHetu("281179-972N")).hasValueSatisfying(huoltaja -> {
                assertThat(huoltaja)
                        .returns(Muutostapa.LISATTY, Huoltaja::getMuutostapa)
                        .returns("1", Huoltaja::getLaji)
                        .returns("2", Huoltaja::getRooli)
                        .returns(null, Huoltaja::getStartDate)
                        .returns(null, Huoltaja::getEndDate);
                assertThat(huoltaja.getOikeudet())
                        .extracting(Oikeus::getMuutostapa, Oikeus::getKoodi, Oikeus::getAlkupvm, Oikeus::getLoppupvm, Oikeus::getHenkiloMuutostietoRivi)
                        .containsExactly(tuple(Muutostapa.LISATTY, "P501", LocalDate.of(1999, 11, 28), null, null));
            });
        });
    }

    @Test
    public void kansalaisuusLisays() {
        String hetu = "281198-911L";

        String tiedostonimi2 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi2, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        Kansalaisuus.builder().muutostapa(Muutostapa.LISATTY).valid(true).code("kansalaisuus2").build()
                ))
                .tiedostoNimi(tiedostonimi2)
                .build());

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu);
        readDto.setKansalaisuus(singleton(KansalaisuusDto.fromKansalaisuusKoodi("kansalaisuus1")));
        when(onrServiceClient.getHenkiloByHetu(eq(hetu))).thenReturn(Optional.of(readDto));

        when(koodistoService.isKoodiValid(eq(Koodisto.MAAT_JA_VALTIOT_2), eq("kansalaisuus1"))).thenReturn(true);
        when(koodistoService.isKoodiValid(eq(Koodisto.MAAT_JA_VALTIOT_2), eq("kansalaisuus2"))).thenReturn(true);

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = captor.getValue();
        assertThat(updateDto.getKansalaisuus())
                .extracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactlyInAnyOrder("kansalaisuus1", "kansalaisuus2");
    }

    @Test
    public void henkilotunnuskorjausYhteinenHetuEiTallennuKaikkiHetutListaan() {
        String yhteinenHetu = "281198-911L";
        String hetu1 = "281198-9540";
        String hetu2 = "281198-9019";

        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, asList(MuutostietoDto.builder()
                        .hetu(hetu1)
                        .tietoryhmat(asList(
                                Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(yhteinenHetu).active(false).build(),
                                Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu1).active(true).build()
                        ))
                        .tiedostoNimi(tiedostonimi1)
                        .build(),
                MuutostietoDto.builder()
                        .hetu(hetu2)
                        .tietoryhmat(asList(
                                Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(yhteinenHetu).active(false).build(),
                                Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu2).active(true).build()
                        ))
                        .tiedostoNimi(tiedostonimi1)
                        .build()));

        HenkiloForceReadDto readDto1 = new HenkiloForceReadDto();
        readDto1.setOidHenkilo("oid1");
        readDto1.setHetu(hetu1);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu1))).thenReturn(Optional.of(readDto1));
        HenkiloForceReadDto readDto2 = new HenkiloForceReadDto();
        readDto2.setOidHenkilo("oid2");
        readDto2.setHetu(hetu2);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu2))).thenReturn(Optional.of(readDto2));

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient, times(2)).updateHenkilo(captor.capture(), eq(true));
        assertThat(captor.getAllValues())
                .extracting(HenkiloUpdateDto::getOidHenkilo, HenkiloUpdateDto::getHetu, HenkiloForceUpdateDto::getKaikkiHetut)
                .containsExactlyInAnyOrder(tuple("oid1", null, singleton(hetu1)), tuple("oid2", null, singleton(hetu2)));
    }

    @Test
    public void uudelleenkasittelyAsettaaProcessTimestampKentat() {
        String hetu1 = "281198-911L";
        String hetu2 = "281198-9540";

        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, asList(
                MuutostietoDto.builder().hetu(hetu1).tiedostoNimi(tiedostonimi1).build(),
                MuutostietoDto.builder().hetu(hetu1).tiedostoNimi(tiedostonimi1).build()));
        String tiedostonimi2 = "test_001.MTT";
        tallennaTiedosto(tiedostonimi2, MuutostietoDto.builder()
                .hetu(hetu1)
                .tiedostoNimi(tiedostonimi2)
                .build());
        String tiedostonimi3 = "test_002.PTT";
        tallennaTiedosto(tiedostonimi3, MuutostietoDto.builder()
                .hetu(hetu2)
                .tiedostoNimi(tiedostonimi3)
                .build());

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu1);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu1))).thenReturn(Optional.of(readDto));
        when(onrServiceClient.getHenkiloByHetu(eq(hetu2))).thenReturn(Optional.empty());

        LocalDateTime time1 = LocalDateTime.of(2018, 12, 5, 11, 31, 46);
        when(timeService.getLocalDateTime()).thenReturn(time1);

        muutostietoService.updateMuutostietos();

        assertThat(henkiloMuutostietoRepository.findAll())
                .extracting(HenkiloMuutostietoRivi::getQueryHetu, HenkiloMuutostietoRivi::getProcessTimestamp)
                .containsExactlyInAnyOrder(tuple(hetu1, time1), tuple(hetu1, time1), tuple(hetu1, time1), tuple(hetu2, null));
        verify(onrServiceClient).getHenkiloByHetu(eq(hetu1));
        verify(onrServiceClient).updateHenkilo(any(HenkiloForceUpdateDto.class), eq(true));

        LocalDateTime time2 = LocalDateTime.of(2019, 4, 3, 23, 12, 59);
        when(timeService.getLocalDateTime()).thenReturn(time2);

        muutostietoService.updateMuutostietos();

        assertThat(henkiloMuutostietoRepository.findAll())
                .extracting(HenkiloMuutostietoRivi::getQueryHetu, HenkiloMuutostietoRivi::getProcessTimestamp)
                .containsExactlyInAnyOrder(tuple(hetu1, time1), tuple(hetu1, time1), tuple(hetu1, time1), tuple(hetu2, time2));
        verify(onrServiceClient).getHenkiloByHetu(eq(hetu2));
        verify(onrServiceClient).updateHenkilo(any(HenkiloForceUpdateDto.class), eq(true));

        String tiedostonimi4 = "test_002.MTT";
        tallennaTiedosto(tiedostonimi4, MuutostietoDto.builder()
                .hetu(hetu1)
                .tiedostoNimi(tiedostonimi4)
                .build());

        LocalDateTime time3 = LocalDateTime.of(2020, 7, 1, 4, 2, 10);
        when(timeService.getLocalDateTime()).thenReturn(time3);

        muutostietoService.updateMuutostietos();

        assertThat(henkiloMuutostietoRepository.findAll())
                .extracting(HenkiloMuutostietoRivi::getQueryHetu, HenkiloMuutostietoRivi::getProcessTimestamp)
                .containsExactlyInAnyOrder(tuple(hetu1, time1), tuple(hetu1, time1), tuple(hetu1, time1), tuple(hetu2, time2), tuple(hetu1, time3));
        verify(onrServiceClient, times(2)).getHenkiloByHetu(eq(hetu1));
        verify(onrServiceClient, times(2)).updateHenkilo(any(HenkiloForceUpdateDto.class), eq(true));
    }

    @Test
    public void uudelleenkasittelyHenkilotunnuskorjausOppijanumerorekisteriEiTunnistaVanhojaHetuja() {
        String hetu1 = "281198-911L";
        String hetu2 = "281198-9540";
        String hetu3 = "281198-9019";

        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, MuutostietoDto.builder()
                .hetu(hetu1)
                .tietoryhmat(asList(
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu1).active(false).build(),
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu2).active(true).build()
                ))
                .tiedostoNimi(tiedostonimi1)
                .build());
        String tiedostonimi2 = "test_001.MTT";
        tallennaTiedosto(tiedostonimi2, MuutostietoDto.builder()
                .hetu(hetu2)
                .tietoryhmat(asList(
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu1).active(false).build(),
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu2).active(false).build(),
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu3).active(true).build()
                ))
                .tiedostoNimi(tiedostonimi2)
                .build());

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu3);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu1))).thenReturn(Optional.empty());
        when(onrServiceClient.getHenkiloByHetu(eq(hetu2))).thenReturn(Optional.empty());
        when(onrServiceClient.getHenkiloByHetu(eq(hetu3))).thenReturn(Optional.of(readDto));

        muutostietoService.updateMuutostietos(); // henkilöä ei löydy hetulla 1 -> mitään tietoryhmiä ei käsitellä
        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        assertThat(captor.getValue())
                .returns(null, HenkiloUpdateDto::getHetu)
                .returns(Stream.of(hetu1, hetu2, hetu3).collect(toSet()), HenkiloForceUpdateDto::getKaikkiHetut);
    }

    @Test
    public void uudelleenkasittelyHenkilotunnuskorjausOppijanumerorekisteriTunnistaaVanhatHetut() {
        String hetu1 = "281198-911L";
        String hetu2 = "281198-9540";
        String hetu3 = "281198-9019";

        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, MuutostietoDto.builder()
                .hetu(hetu1)
                .tietoryhmat(asList(
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu1).active(false).build(),
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu2).active(true).build()
                ))
                .tiedostoNimi(tiedostonimi1)
                .build());
        String tiedostonimi2 = "test_001.MTT";
        tallennaTiedosto(tiedostonimi2, MuutostietoDto.builder()
                .hetu(hetu2)
                .tietoryhmat(asList(
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu1).active(false).build(),
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu2).active(false).build(),
                        Henkilotunnuskorjaus.builder().muutostapa(Muutostapa.LISATTY).hetu(hetu3).active(true).build()
                ))
                .tiedostoNimi(tiedostonimi2)
                .build());

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu3);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu1))).thenReturn(Optional.of(readDto));
        when(onrServiceClient.getHenkiloByHetu(eq(hetu2))).thenReturn(Optional.of(readDto));
        when(onrServiceClient.getHenkiloByHetu(eq(hetu3))).thenReturn(Optional.of(readDto));

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        assertThat(captor.getValue())
                .returns(hetu3, HenkiloUpdateDto::getHetu)
                .returns(Stream.of(hetu1, hetu2, hetu3).collect(toSet()), HenkiloForceUpdateDto::getKaikkiHetut);
    }

    @Test
    public void invalidEmailIsSentAsEmptyStringToOppijanumerorekisteriToAvoidFailingValidation() {
        String hetu = "281198-911L";
        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(SahkopostiOsoite.builder().email("+35891234567").build()))
                .tiedostoNimi(tiedostonimi1)
                .build());

        HenkiloForceReadDto existingHenkilo = new HenkiloForceReadDto();
        existingHenkilo.setHetu(hetu);
        when(onrServiceClient.getHenkiloByHetu(hetu)).thenReturn(Optional.of(existingHenkilo));

        muutostietoService.updateMuutostietos();
        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto update = captor.getValue();

        assertThat(update.getYhteystiedotRyhma()).hasSize(1);
        YhteystiedotRyhmaDto ryhma = update.getYhteystiedotRyhma().stream().findFirst().get();
        assertThat(ryhma.getYhteystieto()).hasSize(1);
        YhteystietoDto yhteystieto = ryhma.getYhteystieto().stream().findFirst().get();
        assertThat(yhteystieto.getYhteystietoTyyppi()).isEqualTo(YhteystietoTyyppi.YHTEYSTIETO_SAHKOPOSTI);
        assertThat(yhteystieto.getYhteystietoArvo()).isEmpty();
    }

    @Test
    public void uudelleenkasittelyTurvakieltoFalseSailyttaaYhteystiedot() {
        String hetu = "281198-911L";

        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        KotimainenOsoite.builder().lahiosoite("osoite1").postinumero("00001").build()
                ))
                .tiedostoNimi(tiedostonimi1)
                .build());
        String tiedostonimi2 = "test_001.MTT";
        tallennaTiedosto(tiedostonimi2, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        Turvakielto.builder().build()
                ))
                .tiedostoNimi(tiedostonimi2)
                .build());
        String tiedostonimi3 = "test_002.MTT";
        tallennaTiedosto(tiedostonimi3, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        Turvakielto.builder().endDate(LocalDate.now().minusDays(1L)).build(),
                        KotimainenOsoite.builder().lahiosoite("osoite2").postinumero("00002").build()
                ))
                .tiedostoNimi(tiedostonimi3)
                .build());

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu))).thenReturn(Optional.of(readDto));

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = captor.getValue();
        assertThat(updateDto)
                .returns(false, HenkiloForceUpdateDto::getTurvakielto)
                .satisfies(t -> {
                    assertThat(t.getYhteystiedotRyhma())
                            .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus)
                            .containsExactly("yhteystietotyyppi4");
                    assertThat(t.getYhteystiedotRyhma().iterator().next().getYhteystieto())
                            .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                            .containsExactlyInAnyOrder(
                                    tuple(YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE, "osoite2"),
                                    tuple(YhteystietoTyyppi.YHTEYSTIETO_POSTINUMERO, "00002")
                            );
                });
    }

    @Test
    public void uudelleenkasittelyTurvakieltoTruePoistaaYhteystiedot() {
        String hetu = "281198-911L";

        String tiedostonimi1 = "test_001.PTT";
        tallennaTiedosto(tiedostonimi1, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        KotimainenOsoite.builder().lahiosoite("osoite1").postinumero("00001").build()
                ))
                .tiedostoNimi(tiedostonimi1)
                .build());
        String tiedostonimi2 = "test_001.MTT";
        tallennaTiedosto(tiedostonimi2, asList(MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        Turvakielto.builder().build()
                ))
                .tiedostoNimi(tiedostonimi2)
                .build(), MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        KotimainenOsoite.builder().lahiosoite("osoite poistettu").postinumero("00000").build()
                ))
                .build()));
        String tiedostonimi3 = "test_002.MTT";
        tallennaTiedosto(tiedostonimi3, MuutostietoDto.builder()
                .hetu(hetu)
                .tietoryhmat(asList(
                        SahkopostiOsoite.builder().email("esimerkki@example.com").build()
                ))
                .build());

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setHetu(hetu);
        when(onrServiceClient.getHenkiloByHetu(eq(hetu))).thenReturn(Optional.of(readDto));

        muutostietoService.updateMuutostietos();

        verify(onrServiceClient).updateHenkilo(captor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = captor.getValue();
        assertThat(updateDto)
                .returns(true, HenkiloForceUpdateDto::getTurvakielto)
                .satisfies(dto -> assertThat(dto.getYhteystiedotRyhma())
                        .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus, ryhma -> ryhma.getYhteystieto().stream().map(YhteystietoDto::getYhteystietoArvo).collect(toList()))
                        .containsExactly(tuple(KoodistoYhteystietoTyyppi.SAHKOINEN_OSOITE.getKoodi(), singletonList("esimerkki@example.com"))));
    }

    private void tallennaTiedosto(String tiedostonimi, MuutostietoDto muutostieto) {
        tallennaTiedosto(tiedostonimi, singletonList(muutostieto));
    }

    private void tallennaTiedosto(String tiedostonimi, List<MuutostietoDto> muutostiedot) {
        tiedostoRepository.findByFileName(tiedostonimi)
                .orElseGet(() -> tiedostoRepository.save(new Tiedosto(tiedostonimi, 0)));
        muutostietoHandleService.importUnprocessedMuutostiedotToDb(muutostiedot, tiedostonimi);
    }

}
