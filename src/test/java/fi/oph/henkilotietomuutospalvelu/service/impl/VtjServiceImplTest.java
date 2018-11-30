package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.client.VtjServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.OrikaConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.OrikaMapperFactory;
import fi.oph.henkilotietomuutospalvelu.mappers.OsoitetietoToYhteystiedotRyhmaConverter;
import fi.oph.henkilotietomuutospalvelu.service.VtjService;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static fi.oph.henkilotietomuutospalvelu.service.validators.UnknownKoodi.HUOLTAJUUSTYYPPI_TUNTEMATON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {OrikaConfiguration.class, OrikaMapperFactory.class, OsoitetietoToYhteystiedotRyhmaConverter.class, VtjServiceImpl.class})
@RunWith(SpringRunner.class)
public class VtjServiceImplTest {
    @Autowired
    private VtjService vtjService;

    @MockBean
    private OnrServiceClient onrServiceClient;

    @MockBean
    private VtjServiceClient vtjServiceClient;

    @Test
    public void hetutonShouldBeSetWithoutExternalCalls() {
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .etunimet("etunimet")
                .build();

        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getYksiloityVTJ)
                .containsExactly(Tuple.tuple("etunimet", null));
        verifyZeroInteractions(this.vtjServiceClient);
        verifyZeroInteractions(this.onrServiceClient);
    }

    @Test
    public void hetullinenYksiloityShouldNotBeAggregatedWithNewInfo() {
        given(this.onrServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("hetu")
                        .yksiloityVTJ(true)
                        .etunimet("etunimi")
                        .kaikkiHetut(new HashSet<>())
                        .build()));
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .hetu("hetu")
                .build();
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getYksiloityVTJ)
                .containsExactly(Tuple.tuple("hetu", null, null));
        verifyZeroInteractions(this.vtjServiceClient);
    }

    @Test
    public void hetullinenYksiloimatonShouldBeAggregatedWithNewInfo() {
        given(this.onrServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("hetu")
                        .yksiloityVTJ(false)
                        .etunimet("etunimi")
                        .kaikkiHetut(new HashSet<>())
                        .build()));
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setHetu("hetu");
        yksiloityHenkilo.setEtunimi("vtjetunimi");
        given(this.vtjServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(yksiloityHenkilo));
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .hetu("hetu")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getYksiloityVTJ)
                .containsExactly(Tuple.tuple("hetu", "vtjetunimi", true));
    }

    @Test
    public void hetullinenNotFoundShouldBeAggregatedWithNewInfo() {
        given(this.onrServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.empty());
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .hetu("hetu")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setHetu("hetu");
        yksiloityHenkilo.setEtunimi("vtjetunimi");
        given(this.vtjServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(yksiloityHenkilo));
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getYksiloityVTJ)
                .containsExactly(Tuple.tuple("hetu", "vtjetunimi", true));
    }

    @Test
    public void allCasesShouldWorkTogether() {
        given(this.onrServiceClient.getHenkiloByHetu("hetuyksiloimaton"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("hetuyksiloimaton")
                        .yksiloityVTJ(false)
                        .etunimet("etunimi")
                        .kaikkiHetut(new HashSet<>())
                        .build()));
        given(this.onrServiceClient.getHenkiloByHetu("hetuyksiloity"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("hetuyksiloity")
                        .yksiloityVTJ(true)
                        .etunimet("etunimi")
                        .kaikkiHetut(new HashSet<>())
                        .build()));
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setHetu("hetuyksiloimaton");
        yksiloityHenkilo.setEtunimi("vtjetunimi");
        given(this.vtjServiceClient.getHenkiloByHetu("hetuyksiloimaton"))
                .willReturn(Optional.of(yksiloityHenkilo));
        HuoltajaCreateDto hetullinenYksiloimatonHuoltaja = HuoltajaCreateDto.builder()
                .hetu("hetuyksiloimaton")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        HuoltajaCreateDto hetutonHuoltaja = HuoltajaCreateDto.builder()
                .etunimet("hetuton")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        HuoltajaCreateDto hetullinenYksiloityHuoltaja = HuoltajaCreateDto.builder()
                .hetu("hetuyksiloity")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(new HashSet<>(Arrays.asList(hetullinenYksiloimatonHuoltaja, hetutonHuoltaja, hetullinenYksiloityHuoltaja)));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getYksiloityVTJ)
                .containsExactlyInAnyOrder(Tuple.tuple("hetuyksiloimaton", "vtjetunimi", true), Tuple.tuple(null, "hetuton", null), Tuple.tuple("hetuyksiloity", null, null));
    }

    @Test
    public void hetullinenYksiloityShouldWorkWithDifferentHetuReturned() {
        given(this.onrServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("erihetu")
                        .yksiloityVTJ(true)
                        .etunimet("etunimi")
                        .kaikkiHetut(Collections.singleton("hetu"))
                        .build()));
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .hetu("hetu")
                .build();
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getYksiloityVTJ)
                .containsExactly(Tuple.tuple("hetu", null, null));
        verifyZeroInteractions(this.vtjServiceClient);
    }

    @Test
    public void passivoituVtjHenkiloShouldBeIgnored() {
        given(this.onrServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("hetu")
                        .yksiloityVTJ(false)
                        .etunimet("etunimi")
                        .kaikkiHetut(new HashSet<>())
                        .build()));
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setHetu("hetu");
        yksiloityHenkilo.setEtunimi("vtjetunimi");
        yksiloityHenkilo.setPassivoitu(true);
        given(this.vtjServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(yksiloityHenkilo));
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .hetu("hetu")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat()).isEmpty();
    }

    @Test
    public void henkiloNotFoundOnVtjShouldBeIgnored() {
        given(this.onrServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.of(HenkiloDto.builder()
                        .hetu("hetu")
                        .yksiloityVTJ(false)
                        .etunimet("etunimi")
                        .kaikkiHetut(new HashSet<>())
                        .build()));
        given(this.vtjServiceClient.getHenkiloByHetu("hetu"))
                .willReturn(Optional.empty());
        HuoltajaCreateDto huoltajaCreateDto = HuoltajaCreateDto.builder()
                .hetu("hetu")
                .huoltajuustyyppiKoodi(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi())
                .build();
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));
        this.vtjService.yksiloiHuoltajatTarvittaessa(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat()).isEmpty();
    }

}