package fi.oph.henkilotietomuutospalvelu.service.impl;

import com.google.common.collect.Lists;
import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.HenkilotunnuskorjausRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TietoryhmaRepository;
import fi.oph.henkilotietomuutospalvelu.service.FileService;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.oph.henkilotietomuutospalvelu.service.TimeService;
import fi.oph.henkilotietomuutospalvelu.service.VtjService;
import fi.oph.henkilotietomuutospalvelu.service.validators.CorrectingHenkiloUpdateValidator;
import fi.vm.sade.oppijanumerorekisteri.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class MuutostietoHandleServiceImplTest {
    @InjectMocks
    private MuutostietoHandleServiceImpl muutostietoHandleService;

    @Mock
    private OnrServiceClient onrServiceClient;

    @Spy
    private FileService fileService = new FileServiceImpl(null, null, null);

    @Spy
    private TimeService timeService = new TimeServiceImpl();

    @Mock
    private HenkiloMuutostietoRepository henkiloMuutostietoRepository;

    @Mock
    private TietoryhmaRepository tietoryhmaRepository;

    @Mock
    private HenkilotunnuskorjausRepository henkilotunnuskorjausRepository;

    @Mock
    private TiedostoRepository tiedostoRepository;

    @Captor
    private ArgumentCaptor<List<HenkiloMuutostietoRivi>> listArgumentCaptor;

    @Captor
    private ArgumentCaptor<HenkiloForceUpdateDto> henkiloForceUpdateDtoArgumentCaptor;

    @Mock
    private CorrectingHenkiloUpdateValidator correctingHenkiloUpdateValidator;

    @Mock
    private KoodistoService koodistoService;

    @Mock
    private VtjService vtjService;

    @Test
    public void lastRowIsNotPresent() {
        String fileName = "FileName";
        Tiedosto tiedosto = new Tiedosto();
        tiedosto.setFileName(fileName);
        tiedosto.setPartCount(1);
        given(this.tiedostoRepository.findByFileName(eq(fileName))).willReturn(Optional.of(tiedosto));

        given(this.henkiloMuutostietoRepository.findLastRowByTiedostoNimi(eq(fileName))).willReturn(Optional.empty());

        MuutostietoDto muutostietoDto = MuutostietoDto.builder()
                .rivi(1)
                .build();
        List<MuutostietoDto> muutostietoDtoList = Lists.newArrayList(muutostietoDto);

        this.muutostietoHandleService.importUnprocessedMuutostiedotToDb(muutostietoDtoList, fileName);

        verify(this.henkiloMuutostietoRepository).saveAll(this.listArgumentCaptor.capture());

        assertThat(this.listArgumentCaptor.getValue())
                .extracting(HenkiloMuutostietoRivi::getRivi)
                .containsExactlyInAnyOrder(1);
    }

    @Test
    public void firstRowIsSkipped() {
        String fileName = "FileName";
        Tiedosto tiedosto = new Tiedosto();
        tiedosto.setFileName(fileName);
        tiedosto.setPartCount(1);
        given(this.tiedostoRepository.findByFileName(eq(fileName))).willReturn(Optional.of(tiedosto));

        given(this.henkiloMuutostietoRepository.findLastRowByTiedostoNimi(eq(fileName)))
                .willReturn(Optional.of(1));

        MuutostietoDto muutostietoDto = MuutostietoDto.builder()
                .rivi(1)
                .build();
        MuutostietoDto muutostietoDto2 = MuutostietoDto.builder()
                .rivi(2)
                .build();
        List<MuutostietoDto> muutostietoDtoList = Lists.newArrayList(muutostietoDto, muutostietoDto2);

        this.muutostietoHandleService.importUnprocessedMuutostiedotToDb(muutostietoDtoList, fileName);

        verify(this.henkiloMuutostietoRepository).saveAll(this.listArgumentCaptor.capture());

        assertThat(this.listArgumentCaptor.getValue())
                .extracting(HenkiloMuutostietoRivi::getRivi)
                .containsExactlyInAnyOrder(2);
    }

    @Test
    public void handleMuutostietoWhenHenkiloNotFound() {
        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");

        given(this.onrServiceClient.getHenkiloByHetu(eq("hetu1"))).willReturn(Optional.empty());

        this.muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        assertThat(henkiloMuutostietoRivi)
                .extracting(HenkiloMuutostietoRivi::getProcessTimestamp)
                .doesNotContainNull();
    }

    @Test
    public void handleMuutostietoWhenHenkiloNotFoundAndOtherHetusNotFound() {
        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");

        HenkiloMuutostietoRivi henkiloMuutostietoRiviOtherHetu = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRiviOtherHetu.setQueryHetu("hetu2");

        given(this.onrServiceClient.getHenkiloByHetu(eq("hetu1"))).willReturn(Optional.empty());
        given(this.onrServiceClient.getHenkiloByHetu(eq("hetu2"))).willReturn(Optional.empty());
        given(this.henkiloMuutostietoRepository.findHenkiloMuutostietoRiviByQueryHetu(eq("hetu1")))
                .willReturn(Lists.newArrayList(henkiloMuutostietoRiviOtherHetu));
        this.muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        assertThat(henkiloMuutostietoRivi)
                .extracting(HenkiloMuutostietoRivi::getProcessTimestamp)
                .doesNotContainNull();
    }

    @Test
    public void handleMuutostietoWhenFirstQueryHetuNotFoundAndTietoryhmaIsKutsumanimi() {
        Tietoryhma tietoryhma = Kutsumanimi.builder()
                .name("kutsumanimi")
                .startDate(LocalDate.now().minusDays(1))
                .build();
        Henkilotunnuskorjaus henkilotunnuskorjaus = Henkilotunnuskorjaus.builder()
                .hetu("hetu2")
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(tietoryhma);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloMuutostietoRivi henkiloMuutostietoRiviOtherHetu = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRiviOtherHetu.setTiedosto(new Tiedosto());
        henkiloMuutostietoRiviOtherHetu.setQueryHetu("hetu1");
        henkiloMuutostietoRiviOtherHetu.addTietoryhma(henkilotunnuskorjaus);

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .hetu("hetu2")
                .oidHenkilo("oid1")
                .etunimet("etunimi kutsumanimi")
                .build();

        given(this.onrServiceClient.getHenkiloByHetu(eq("hetu1"))).willReturn(Optional.empty());
        given(this.onrServiceClient.getHenkiloByHetu(eq("hetu2"))).willReturn(Optional.of(henkiloDto));
        given(this.henkiloMuutostietoRepository.findHenkiloMuutostietoRiviByQueryHetu(eq("hetu1")))
                .willReturn(Lists.newArrayList(henkiloMuutostietoRiviOtherHetu, henkiloMuutostietoRivi));

        this.muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);
        verify(this.onrServiceClient).updateHenkilo(this.henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));

        assertThat(henkiloMuutostietoRivi)
                .extracting(HenkiloMuutostietoRivi::getProcessTimestamp)
                .doesNotContainNull();
        assertThat(henkiloMuutostietoRivi.getQueryHetu())
                .isEqualTo(henkiloMuutostietoRiviOtherHetu.getQueryHetu())
                .isEqualTo("hetu2");

        assertThat(this.henkiloForceUpdateDtoArgumentCaptor.getValue())
                .extracting(HenkiloForceUpdateDto::getOidHenkilo, HenkiloForceUpdateDto::getKutsumanimi)
                .containsExactly("oid1", "kutsumanimi");
    }

    @Test
    public void handleMuutostietoWhenTietoryhmaIsKutsumanimi() {
        Tietoryhma tietoryhma = Kutsumanimi.builder()
                .name("kutsumanimi")
                .startDate(LocalDate.now().minusDays(1))
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(tietoryhma);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .oidHenkilo("oid1")
                .etunimet("etunimi kutsumanimi")
                .build();

        given(this.onrServiceClient.getHenkiloByHetu(eq("hetu1"))).willReturn(Optional.of(henkiloDto));

        this.muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);
        verify(this.onrServiceClient).updateHenkilo(this.henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));

        assertThat(henkiloMuutostietoRivi)
                .extracting(HenkiloMuutostietoRivi::getProcessTimestamp)
                .doesNotContainNull();

        assertThat(this.henkiloForceUpdateDtoArgumentCaptor.getValue())
                .extracting(HenkiloForceUpdateDto::getOidHenkilo, HenkiloForceUpdateDto::getKutsumanimi)
                .containsExactly("oid1", "kutsumanimi");
    }

    @Test
    public void handleMuutostietoKotimainenOsoiteKorjattu() {
        Tietoryhma osoiteKorjattu = KotimainenOsoite.builder()
                .muutostapa(Muutostapa.KORJATTU)
                .lahiosoite("osoiteKorjattu")
                .postinumero("00001")
                .build();
        Tietoryhma osoiteKorjattavaa = KotimainenOsoite.builder()
                .muutostapa(Muutostapa.KORJATTAVAA)
                .lahiosoite("osoiteKorjattavaa")
                .postinumero("00002")
                .build();
        Tietoryhma osoiteLisatieto = KotimainenOsoite.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .lahiosoite("osoiteLisatieto")
                .postinumero("00003")
                .build();
        Tietoryhma osoitePoistettu = KotimainenOsoite.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .lahiosoite("osoitePoistettu")
                .postinumero("00004")
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(osoiteKorjattu, osoiteKorjattavaa, osoiteLisatieto, osoitePoistettu);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .yhteystiedotRyhma(new HashSet<>())
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getYhteystiedotRyhma()).hasOnlyOneElementSatisfying(yhteystietoryhma -> assertThat(yhteystietoryhma.getYhteystieto())
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE, "osoiteKorjattu"),
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_POSTINUMERO, "00001")
                ));
    }

    @Test
    public void handleMuutostietoTilapainenOsoitePoistettu() {
        Tietoryhma osoitePoistettu = TilapainenKotimainenOsoite.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .lahiosoite("osoitePoistettu")
                .postinumero("00001")
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(osoitePoistettu);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .yhteystiedotRyhma(Stream.of(
                        YhteystiedotRyhmaDto.builder()
                                .ryhmaAlkuperaTieto("alkupera1")
                                .ryhmaKuvaus("yhteystietotyyppi9")
                                .yhteystieto(YhteystietoDto.builder()
                                        .yhteystietoTyyppi(YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE)
                                        .yhteystietoArvo("osoiteTilapainen")
                                        .build())
                                .build(),
                        YhteystiedotRyhmaDto.builder()
                                .ryhmaAlkuperaTieto("alkupera2")
                                .ryhmaKuvaus("yhteystietotyyppi2")
                                .yhteystieto(YhteystietoDto.builder()
                                        .yhteystietoTyyppi(YhteystietoTyyppi.YHTEYSTIETO_SAHKOPOSTI)
                                        .yhteystietoArvo("tyosahkopostiosoite@example.com")
                                        .build())
                                .build()
                ).collect(Collectors.toSet()))
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getYhteystiedotRyhma()).hasOnlyOneElementSatisfying(yhteystietoryhma -> {
            assertThat(yhteystietoryhma.getRyhmaAlkuperaTieto()).isEqualTo("alkupera2");
            assertThat(yhteystietoryhma.getRyhmaKuvaus()).isEqualTo("yhteystietotyyppi2");
            assertThat(yhteystietoryhma.getYhteystieto())
                    .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                    .containsExactly(tuple(YhteystietoTyyppi.YHTEYSTIETO_SAHKOPOSTI, "tyosahkopostiosoite@example.com"));
        });
    }

    @Test
    public void handleMuutostietoKotimainenOsoiteMuutettu() {
        Tietoryhma osoiteLisatty = TilapainenKotimainenOsoite.builder()
                .muutostapa(Muutostapa.LISATTY)
                .lahiosoite("osoiteLisatty")
                .postinumero("00001")
                .startDate(LocalDate.now())
                .build();
        Tietoryhma osoiteMuutettu = TilapainenKotimainenOsoite.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .lahiosoite("osoiteMuutettu")
                .postinumero("00002")
                .startDate(LocalDate.now().minusYears(1))
                .endDate(LocalDate.now().minusDays(1))
                .build();
        Tietoryhma osoiteLisatieto = TilapainenKotimainenOsoite.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .lahiosoite("osoiteLisatieto")
                .postinumero("00003")
                .startDate(LocalDate.now())
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(osoiteLisatty, osoiteMuutettu, osoiteLisatieto);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .yhteystiedotRyhma(new HashSet<>())
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getYhteystiedotRyhma()).hasOnlyOneElementSatisfying(yhteystietoryhma -> assertThat(yhteystietoryhma.getYhteystieto())
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE, "osoiteLisatty"),
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_POSTINUMERO, "00001")
                ));
    }

    @Test
    public void handleMuutostietoEtunimiJaKutsumanimiMuutos() {
        Tietoryhma etunimi = HenkiloNameChange.builder()
                .name("Mor'jes A.A.")
                .nameType(NameType.ETUNIMI)
                .startDate(LocalDate.now().minusDays(1))
                .build();
        Tietoryhma kutsumanimi = Kutsumanimi.builder()
                .name("Mor'jes")
                .startDate(LocalDate.now().minusDays(1))
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(etunimi, kutsumanimi);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder().build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getEtunimet()).isEqualTo("Mor'jes A.A.");
        assertThat(updateDto.getKutsumanimi()).isEqualTo("Mor'jes");
    }

    @Test
    public void handleMuutostietoEtunimiMuutosKutsumanimiPysyy() {
        Tietoryhma henkiloName = HenkiloNameChange.builder()
                .name("uusietunimi toinennimi")
                .nameType(NameType.ETUNIMI)
                .startDate(LocalDate.now().minusDays(1))
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(henkiloName);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("vanhanimi toinennimi")
                .kutsumanimi("toinennimi")
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getEtunimet()).isEqualTo("uusietunimi toinennimi");
        assertThat(updateDto.getKutsumanimi()).isNull();
    }

    @Test
    public void handleMuutostietoEtunimiMuutosKutsumanimiMuuttuu() {
        Tietoryhma henkiloName = HenkiloNameChange.builder()
                .name("uusietunimi toinennimi")
                .nameType(NameType.ETUNIMI)
                .startDate(LocalDate.now().minusDays(1))
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(henkiloName);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("vanhanimi toinennimi")
                .kutsumanimi("vanhanimi")
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getEtunimet()).isEqualTo("uusietunimi toinennimi");
        assertThat(updateDto.getKutsumanimi()).isEqualTo("uusietunimi toinennimi");
    }

    @Test
    public void handleMuutostietoKutsumanimiMuutos() {
        Tietoryhma henkiloName = Kutsumanimi.builder()
                .name("toinennimi")
                .startDate(LocalDate.now().minusDays(1))
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(henkiloName);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("vanhanimi toinennimi")
                .kutsumanimi("vanhanimi")
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getEtunimet()).isNull();
        assertThat(updateDto.getKutsumanimi()).isEqualTo("toinennimi");
    }

    @Test
    public void handleMuutostietoKutsumanimiMuutosViallinen() {
        Tietoryhma henkiloName = Kutsumanimi.builder()
                .name("uusinimi")
                .startDate(LocalDate.now().minusDays(1))
                .build();

        HenkiloMuutostietoRivi henkiloMuutostietoRivi = new HenkiloMuutostietoRivi();
        henkiloMuutostietoRivi.setTiedosto(new Tiedosto());
        henkiloMuutostietoRivi.setQueryHetu("hetu1");
        henkiloMuutostietoRivi.addTietoryhma(henkiloName);
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any())).thenReturn(singletonList(henkiloMuutostietoRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("vanhanimi toinennimi")
                .kutsumanimi("vanhanimi")
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq("hetu1"))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(henkiloMuutostietoRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getEtunimet()).isNull();
        assertThat(updateDto.getKutsumanimi()).isEqualTo("vanhanimi toinennimi");
    }

    @Test
    public void handleUusiHuoltajaTieto() {
        Tietoryhma huoltajatieto = Huoltaja.builder()
                .hetu("huoltajanhetu")
                .startDate(LocalDate.now().minus(1L, ChronoUnit.YEARS))
                .endDate(LocalDate.now().plus(17L, ChronoUnit.YEARS))
                .muutostapa(Muutostapa.LISATTY)
                .build();
        HenkiloMuutostietoRivi muutosRivi = new HenkiloMuutostietoRivi();
        muutosRivi.addTietoryhma(huoltajatieto);
        muutosRivi.setQueryHetu("huollettavanhetu");
        muutosRivi.setTiedosto(new Tiedosto());
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any()))
                .thenReturn(singletonList(muutosRivi));

        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .hetu("huollettavanhetu")
                .huoltajat(new HashSet<>())
                .build();
        when(onrServiceClient.getHenkiloByHetu(eq(henkiloDto.getHetu()))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(muutosRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getHuoltajat().size()).isEqualTo(1);
    }

    @Test
    public void handlePoistettuHuoltajaTieto() {
        Tietoryhma huoltajatieto = Huoltaja.builder()
                .hetu("huoltajanhetu")
                .startDate(LocalDate.now().minus(18L, ChronoUnit.YEARS))
                .endDate(LocalDate.now().minus(1L, ChronoUnit.DAYS))
                .muutostapa(Muutostapa.POISTETTU)
                .build();
        HenkiloMuutostietoRivi muutosRivi = new HenkiloMuutostietoRivi();
        muutosRivi.addTietoryhma(huoltajatieto);
        muutosRivi.setQueryHetu("huollettavanhetu");
        muutosRivi.setTiedosto(new Tiedosto());
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any()))
                .thenReturn(singletonList(muutosRivi));

        HashSet<HuoltajaCreateDto> huoltajat = new HashSet<>(
                singletonList(HuoltajaCreateDto.builder().hetu("huoltajanhetu").build()));
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .hetu("huollettavanhetu")
                .huoltajat(huoltajat).build();
        when(onrServiceClient.getHenkiloByHetu(eq(henkiloDto.getHetu()))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(muutosRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getHuoltajat()).isEmpty();
    }

    @Test
    public void handleMuuttunutHuoltajaTieto() {
        Tietoryhma uusiHuoltajatieto = Huoltaja.builder()
                .hetu("huoltajanhetu")
                .startDate(LocalDate.now().minus(1L, ChronoUnit.DAYS))
                .endDate(LocalDate.now().plus(8L, ChronoUnit.YEARS))
                .muutostapa(Muutostapa.LISATTY)
                .build();
        Tietoryhma vanhaHuoltajatieto = Huoltaja.builder()
                .hetu("huoltajanhetu")
                .startDate(LocalDate.now().minus(10L, ChronoUnit.YEARS))
                .endDate(LocalDate.now().minus(2L, ChronoUnit.DAYS))
                .muutostapa(Muutostapa.MUUTETTU)
                .build();
        HenkiloMuutostietoRivi muutosRivi = new HenkiloMuutostietoRivi();
        muutosRivi.addTietoryhma(uusiHuoltajatieto);
        muutosRivi.addTietoryhma(vanhaHuoltajatieto);
        muutosRivi.setQueryHetu("huollettavanhetu");
        muutosRivi.setTiedosto(new Tiedosto());
        when(henkiloMuutostietoRepository.findByQueryHetuInAndProcessTimestampIsNull(any()))
                .thenReturn(singletonList(muutosRivi));

        HashSet<HuoltajaCreateDto> huoltajat = new HashSet<>(
                singletonList(HuoltajaCreateDto.builder().hetu("huoltajanhetu").build()));
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .hetu("huollettavanhetu")
                .huoltajat(huoltajat).build();
        when(onrServiceClient.getHenkiloByHetu(eq(henkiloDto.getHetu()))).thenReturn(Optional.of(henkiloDto));

        muutostietoHandleService.handleMuutostieto(muutosRivi);

        verify(onrServiceClient).updateHenkilo(henkiloForceUpdateDtoArgumentCaptor.capture(), eq(true));
        HenkiloForceUpdateDto updateDto = henkiloForceUpdateDtoArgumentCaptor.getValue();
        assertThat(updateDto.getHuoltajat().size()).isEqualTo(1);
    }

}
