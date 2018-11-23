package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.client.VtjServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.OrikaConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.OrikaMapperFactory;
import fi.oph.henkilotietomuutospalvelu.mappers.HenkiloDuplicateDtoConverter;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static fi.oph.henkilotietomuutospalvelu.service.validators.UnknownKoodi.HUOLTAJUUSTYYPPI_TUNTEMATON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {OrikaConfiguration.class, OrikaMapperFactory.class, HenkiloDuplicateDtoConverter.class, VtjServiceImpl.class})
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
        this.vtjService.rikastaHuoltajatVtjTiedoilla(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet)
                .containsExactly("etunimet");
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
        this.vtjService.rikastaHuoltajatVtjTiedoilla(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet)
                .containsExactly(Tuple.tuple("hetu", null));
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
        this.vtjService.rikastaHuoltajatVtjTiedoilla(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet)
                .containsExactly(Tuple.tuple("hetu", "vtjetunimi"));
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
        this.vtjService.rikastaHuoltajatVtjTiedoilla(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getEtunimet)
                .containsExactly(Tuple.tuple("hetu", "vtjetunimi"));
    }
}