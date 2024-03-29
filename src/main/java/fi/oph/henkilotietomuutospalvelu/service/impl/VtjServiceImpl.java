package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.OnrServiceClient;
import fi.oph.henkilotietomuutospalvelu.client.VtjServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.OrikaConfiguration;
import fi.oph.henkilotietomuutospalvelu.service.VtjService;
import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import fi.oph.henkilotietomuutospalvelu.utils.HenkiloUtils;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VtjServiceImpl implements VtjService {
    private final VtjServiceClient vtjServiceClient;
    private final OnrServiceClient onrServiceClient;

    private final OrikaConfiguration orikaConfiguration;

    public static final String RYHMAALKUPERA_VTJ = "alkupera1";
    public static final String RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE = "yhteystietotyyppi8";

    @Override
    public void yksiloiHuoltajatTarvittaessa(HenkiloForceUpdateDto henkiloForceUpdateDto) {
        // Hetulliset yksilöidyt
        Set<String> ennestaanLoytyvatYksiloidytHetut = henkiloForceUpdateDto.getHuoltajat().stream()
                .filter(huoltajaCreateDto -> StringUtils.hasLength(huoltajaCreateDto.getHetu()))
                .map(huoltajaCreateDto -> onrServiceClient.getHenkiloByHetu(huoltajaCreateDto.getHetu()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(HenkiloForceReadDto::isYksiloityVTJ)
                .flatMap(henkiloDto -> Stream.concat(Stream.of(henkiloDto.getHetu()), henkiloDto.getKaikkiHetut().stream()))
                .collect(Collectors.toSet());
        Stream<HuoltajaCreateDto> hetullisetYksiloidytHuoltajat = henkiloForceUpdateDto.getHuoltajat().stream()
                .filter(huoltajaCreateDto -> ennestaanLoytyvatYksiloidytHetut.contains(huoltajaCreateDto.getHetu()));
        // Hetulliset ei yksilöidyt
        Stream<HuoltajaCreateDto> rikastetutHetulisetHuoltajat = henkiloForceUpdateDto.getHuoltajat().stream()
                .filter(huoltajaCreateDto -> StringUtils.hasLength(huoltajaCreateDto.getHetu()))
                .filter(huoltajaCreateDto -> !ennestaanLoytyvatYksiloidytHetut.contains(huoltajaCreateDto.getHetu()))
                .map(huoltajaCreateDto -> vtjServiceClient.getHenkiloByHetu(huoltajaCreateDto.getHetu()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(this::filterPassivoitu)
                .map(yksiloityHenkilo -> this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class))
                .map(this::setKutsumanimi);
        // Hetuttomat (ei tehdä muutoksia)
        Stream<HuoltajaCreateDto> hetuttomatHuoltajat = henkiloForceUpdateDto.getHuoltajat().stream()
                .filter(huoltajaCreateDto -> StringUtils.isEmpty(huoltajaCreateDto.getHetu()));

        Set<HuoltajaCreateDto> huoltajaCreateDtos = Stream.of(rikastetutHetulisetHuoltajat, hetuttomatHuoltajat, hetullisetYksiloidytHuoltajat)
                .flatMap(streamOfStreams -> streamOfStreams)
                .collect(Collectors.toSet());
        henkiloForceUpdateDto.setHuoltajat(huoltajaCreateDtos);
    }

    private HuoltajaCreateDto setKutsumanimi(HuoltajaCreateDto huoltajaCreateDto) {
        if (huoltajaCreateDto.getKutsumanimi() == null || !HenkiloUpdateUtil.isValidKutsumanimi(
                huoltajaCreateDto.getEtunimet(), huoltajaCreateDto.getKutsumanimi())) {
            huoltajaCreateDto.setKutsumanimi(huoltajaCreateDto.getEtunimet());
        }
        return huoltajaCreateDto;
    }

    private boolean filterPassivoitu(YksiloityHenkilo yksiloityHenkilo) {
        if (yksiloityHenkilo.isPassivoitu()) {
            log.warn("Got passivoitu huoltaja from VTJ with hetu {}", yksiloityHenkilo.getHetu());
        }
        return !yksiloityHenkilo.isPassivoitu();
    }
}
