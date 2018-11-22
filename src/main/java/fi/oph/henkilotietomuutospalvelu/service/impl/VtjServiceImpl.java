package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.client.VtjServiceClient;
import fi.oph.henkilotietomuutospalvelu.config.OrikaConfiguration;
import fi.oph.henkilotietomuutospalvelu.service.VtjService;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.oph.henkilotietomuutospalvelu.service.validators.UnknownKoodi.HUOLTAJUUSTYYPPI_TUNTEMATON;

@Service
@RequiredArgsConstructor
public class VtjServiceImpl implements VtjService {
    private final VtjServiceClient vtjServiceClient;

    private final OrikaConfiguration orikaConfiguration;

    public static final String RYHMAALKUPERA_VTJ = "alkupera1";
    public static final String RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE = "yhteystietotyyppi8";

    @Override
    public void rikastaHuoltajatVtjTiedoilla(HenkiloForceUpdateDto henkiloForceUpdateDto) {
        // Hetulliset
        Stream<HuoltajaCreateDto> rikastetutHetulisetHuoltajat = henkiloForceUpdateDto.getHuoltajat().stream()
                .filter(huoltajaCreateDto -> StringUtils.hasLength(huoltajaCreateDto.getHetu()))
                .map(huoltajaCreateDto -> vtjServiceClient.getHenkiloByHetu(huoltajaCreateDto.getHetu()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(YksiloityHenkilo::isPassivoitu)
                .map(yksiloityHenkilo -> this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class))
                .map(huoltajaCreateDto -> this.setHuoltajuusTyyppikoodit(huoltajaCreateDto, henkiloForceUpdateDto.getHuoltajat()));
        // Hetuttomat (ei tehdä muutoksia)
        Stream<HuoltajaCreateDto> hetuttomatHuoltajat = henkiloForceUpdateDto.getHuoltajat().stream()
                .filter(huoltajaCreateDto -> StringUtils.isEmpty(huoltajaCreateDto.getHetu()));
        henkiloForceUpdateDto.setHuoltajat(Stream.concat(rikastetutHetulisetHuoltajat, hetuttomatHuoltajat).collect(Collectors.toSet()));
    }

    private HuoltajaCreateDto setHuoltajuusTyyppikoodit(HuoltajaCreateDto huoltajaCreateDto, Collection<HuoltajaCreateDto> kaikkiHuoltajat) {
        String huoltajuustyyppikoodi = kaikkiHuoltajat.stream()
                .filter(huoltaja -> huoltaja.getHetu().equals(huoltajaCreateDto.getHetu()))
                .map(HuoltajaCreateDto::getHuoltajuustyyppiKoodi)
                .findFirst()
                .orElse(HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi());
        huoltajaCreateDto.setHuoltajuustyyppiKoodi(huoltajuustyyppikoodi);
        return huoltajaCreateDto;
    }

}
