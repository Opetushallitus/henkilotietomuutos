package fi.oph.henkilotietomuutospalvelu.config;

import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import org.jresearch.orika.spring.OrikaSpringMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.oph.henkilotietomuutospalvelu.service.impl.VtjServiceImpl.RYHMAALKUPERA_VTJ;
import static fi.oph.henkilotietomuutospalvelu.service.impl.VtjServiceImpl.RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE;

@Component
@RequiredArgsConstructor
public class OrikaConfiguration extends OrikaSpringMapper {
    private final MapperFactory factory;

    @Override
    protected void configure() {
        super.configure();
        this.factory.classMap(YksiloityHenkilo.class, HuoltajaCreateDto.class)
                .field("etunimi", "etunimet")
                .field("kansalaisuusKoodit", "kansalaisuusKoodi")
                .field("osoitteet", "yhteystiedotRyhma")
                .byDefault()
                .customize(new CustomMapper<YksiloityHenkilo, HuoltajaCreateDto>() {
                    @Override
                    public void mapAtoB(YksiloityHenkilo yksiloityHenkilo, HuoltajaCreateDto huoltajaCreateDto, MappingContext context) {
                        if (huoltajaCreateDto.getYhteystiedotRyhma() == null) {
                            huoltajaCreateDto.setYhteystiedotRyhma(new HashSet<>());
                        }
                        if (StringUtils.hasLength(yksiloityHenkilo.getSahkoposti())) {
                            huoltajaCreateDto.getYhteystiedotRyhma().add(YhteystiedotRyhmaDto.builder()
                                    .ryhmaAlkuperaTieto(RYHMAALKUPERA_VTJ)
                                    .ryhmaKuvaus(RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE)
                                    .readOnly(true)
                                    .yhteystieto(YhteystietoDto.builder()
                                            .yhteystietoArvo(yksiloityHenkilo.getSahkoposti())
                                            .yhteystietoTyyppi(YhteystietoTyyppi.YHTEYSTIETO_SAHKOPOSTI)
                                            .build())
                                    .build());
                        }
                        huoltajaCreateDto.setYhteystiedotRyhma(huoltajaCreateDto.getYhteystiedotRyhma().stream()
                                .filter(yhteystiedotRyhmaDto -> !CollectionUtils.isEmpty(yhteystiedotRyhmaDto.getYhteystieto()))
                                .collect(Collectors.toSet()));
                    }
                })
                .register();
    }
}
