package fi.oph.henkilotietomuutospalvelu.mappers;

import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.oph.henkilotietomuutospalvelu.service.impl.VtjServiceImpl.RYHMAALKUPERA_VTJ;

@Component
public class HenkiloDuplicateDtoConverter extends CustomConverter<YksiloityHenkilo.OsoiteTieto, YhteystiedotRyhmaDto> {

    @Override
    public YhteystiedotRyhmaDto convert(YksiloityHenkilo.OsoiteTieto osoiteTieto, Type<? extends YhteystiedotRyhmaDto> type, MappingContext mappingContext) {
        YhteystiedotRyhmaDto yhteystiedotRyhmaDto = new YhteystiedotRyhmaDto();
        yhteystiedotRyhmaDto.setReadOnly(true);
        yhteystiedotRyhmaDto.setRyhmaAlkuperaTieto(RYHMAALKUPERA_VTJ);
        yhteystiedotRyhmaDto.setRyhmaKuvaus(osoiteTieto.getTyyppi());
        Set<YhteystietoDto> yhteystietoDtos = Stream.of(
                new YhteystietoDto(YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE, this.haeArvoKielenMukaan(osoiteTieto.getKatuosoiteS(), osoiteTieto.getKatuosoiteR())),
                new YhteystietoDto(YhteystietoTyyppi.YHTEYSTIETO_POSTINUMERO, osoiteTieto.getPostinumero()),
                new YhteystietoDto(YhteystietoTyyppi.YHTEYSTIETO_KAUPUNKI, this.haeArvoKielenMukaan(osoiteTieto.getKaupunkiS(), osoiteTieto.getKaupunkiR())),
                new YhteystietoDto(YhteystietoTyyppi.YHTEYSTIETO_MAA, this.haeArvoKielenMukaan(osoiteTieto.getMaaS(), osoiteTieto.getMaaR()))
        )
                .filter(yhteystietoDto -> StringUtils.hasLength(yhteystietoDto.getYhteystietoArvo()))
                .collect(Collectors.toSet());
        yhteystiedotRyhmaDto.setYhteystieto(yhteystietoDtos);
        return yhteystiedotRyhmaDto;
    }

    // Käytetään ensisijaisesti suomenkielistä nimeä jos se löytyy. Muuten ruotsinkielistä.
    private String haeArvoKielenMukaan(String yhteystietoSuomeksi, String yhteystietoRuotsiksi) {
        if (StringUtils.hasLength(yhteystietoSuomeksi)) {
            return yhteystietoSuomeksi;
        }
        return yhteystietoRuotsiksi;
    }
}
