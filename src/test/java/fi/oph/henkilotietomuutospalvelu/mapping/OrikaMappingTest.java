package fi.oph.henkilotietomuutospalvelu.mapping;

import fi.oph.henkilotietomuutospalvelu.config.OrikaConfiguration;
import fi.oph.henkilotietomuutospalvelu.config.OrikaMapperFactory;
import fi.oph.henkilotietomuutospalvelu.mappers.OsoitetietoToYhteystiedotRyhmaConverter;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoDto;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;

import static fi.oph.henkilotietomuutospalvelu.service.impl.VtjServiceImpl.RYHMAALKUPERA_VTJ;
import static fi.oph.henkilotietomuutospalvelu.service.impl.VtjServiceImpl.RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE;
import static fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {OrikaConfiguration.class, OrikaMapperFactory.class, OsoitetietoToYhteystiedotRyhmaConverter.class})
@RunWith(SpringRunner.class)
public class OrikaMappingTest {
    @Autowired
    private OrikaConfiguration orikaConfiguration;

    @Test
    public void yksiloityHenkiloShouldMapToHuoltajaCreateDto() {
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setEtunimi("etunimi");
        yksiloityHenkilo.setSukunimi("sukunimi");
        yksiloityHenkilo.setKutsumanimi(null);
        yksiloityHenkilo.setHetu("hetu");
        yksiloityHenkilo.setPassivoitu(false);
        yksiloityHenkilo.setSukupuoli("sukupuoli");
        yksiloityHenkilo.setHuoltajat(new ArrayList<>());
        yksiloityHenkilo.setKotikunta("kotikunta");
        yksiloityHenkilo.setTurvakielto(true);
        yksiloityHenkilo.setAidinkieliKoodi("äidinkieli");
        yksiloityHenkilo.setSahkoposti("sähköposti");

        yksiloityHenkilo.addKansalaisuusKoodi("kansalaisuus");
        YksiloityHenkilo.OsoiteTieto osoiteTieto = new YksiloityHenkilo.OsoiteTieto("tyyppi", "katusuomi", "katuruotsi", "postinumero", "kaupunkisuomi", "kaupunkiruotsi", "maasuomi", "maaruotsi");
        yksiloityHenkilo.addOsoiteTieto(osoiteTieto);
        HuoltajaCreateDto huoltajaCreateDto = this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class);

        assertThat(huoltajaCreateDto)
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi, HuoltajaCreateDto::getKutsumanimi, HuoltajaCreateDto::getHetu, HuoltajaCreateDto::getKansalaisuusKoodi)
                .containsExactly("etunimi", "sukunimi", "etunimi", "hetu", Collections.singleton("kansalaisuus"));
        assertThat(huoltajaCreateDto.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaAlkuperaTieto, YhteystiedotRyhmaDto::isReadOnly, YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactlyInAnyOrder(Tuple.tuple(RYHMAALKUPERA_VTJ, true, "tyyppi"),
                        Tuple.tuple(RYHMAALKUPERA_VTJ, true, RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE));

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma().stream().flatMap(yhteystiedotRyhmaDto -> yhteystiedotRyhmaDto.getYhteystieto().stream()))
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(YHTEYSTIETO_KATUOSOITE, "katusuomi"),
                        Tuple.tuple(YHTEYSTIETO_POSTINUMERO, "postinumero"),
                        Tuple.tuple(YHTEYSTIETO_KAUPUNKI, "kaupunkisuomi"),
                        Tuple.tuple(YHTEYSTIETO_MAA, "maasuomi"),
                        Tuple.tuple(YHTEYSTIETO_SAHKOPOSTI, "sähköposti")
                );
    }

    @Test
    public void swedishAddressShouldBeUsedIfFinnishIsNotProvided() {
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setSahkoposti("sähköposti");

        YksiloityHenkilo.OsoiteTieto osoiteTieto = new YksiloityHenkilo.OsoiteTieto("tyyppi", "", "katuruotsi", "postinumero", "", "kaupunkiruotsi", "", "maaruotsi");
        yksiloityHenkilo.addOsoiteTieto(osoiteTieto);
        HuoltajaCreateDto huoltajaCreateDto = this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class);

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaAlkuperaTieto, YhteystiedotRyhmaDto::isReadOnly, YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactlyInAnyOrder(Tuple.tuple(RYHMAALKUPERA_VTJ, true, "tyyppi"),
                        Tuple.tuple(RYHMAALKUPERA_VTJ, true, RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE));

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma().stream().flatMap(yhteystiedotRyhmaDto -> yhteystiedotRyhmaDto.getYhteystieto().stream()))
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(YHTEYSTIETO_KATUOSOITE, "katuruotsi"),
                        Tuple.tuple(YHTEYSTIETO_POSTINUMERO, "postinumero"),
                        Tuple.tuple(YHTEYSTIETO_KAUPUNKI, "kaupunkiruotsi"),
                        Tuple.tuple(YHTEYSTIETO_MAA, "maaruotsi"),
                        Tuple.tuple(YHTEYSTIETO_SAHKOPOSTI, "sähköposti")
                );
    }

    @Test
    public void yhteystietoRyhmaWithoutYhteystietoValuesShouldNotBeUsed() {
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setSahkoposti("sähköposti");

        YksiloityHenkilo.OsoiteTieto osoiteTieto = new YksiloityHenkilo.OsoiteTieto("tyyppi", "", "", "", "", "", "", "");
        yksiloityHenkilo.addOsoiteTieto(osoiteTieto);
        HuoltajaCreateDto huoltajaCreateDto = this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class);

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaAlkuperaTieto, YhteystiedotRyhmaDto::isReadOnly, YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactlyInAnyOrder(Tuple.tuple(RYHMAALKUPERA_VTJ, true, RYHMAKUVAUS_VTJ_SAHKOINEN_OSOITE));

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma().stream().flatMap(yhteystiedotRyhmaDto -> yhteystiedotRyhmaDto.getYhteystieto().stream()))
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(YHTEYSTIETO_SAHKOPOSTI, "sähköposti")
                );
    }

    @Test
    public void yhteystietoRyhmasShouldBeEmpty() {
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();
        yksiloityHenkilo.setSahkoposti("");

        YksiloityHenkilo.OsoiteTieto osoiteTieto = new YksiloityHenkilo.OsoiteTieto("tyyppi", "", "", "", "", "", "", "");
        yksiloityHenkilo.addOsoiteTieto(osoiteTieto);
        HuoltajaCreateDto huoltajaCreateDto = this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class);

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaAlkuperaTieto, YhteystiedotRyhmaDto::isReadOnly, YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .isEmpty();
    }

    @Test
    public void defaultYhteystietoRyhmasShouldBeEmpty() {
        YksiloityHenkilo yksiloityHenkilo = new YksiloityHenkilo();

        HuoltajaCreateDto huoltajaCreateDto = this.orikaConfiguration.map(yksiloityHenkilo, HuoltajaCreateDto.class);

        assertThat(huoltajaCreateDto.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaAlkuperaTieto, YhteystiedotRyhmaDto::isReadOnly, YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .isEmpty();
    }
}
