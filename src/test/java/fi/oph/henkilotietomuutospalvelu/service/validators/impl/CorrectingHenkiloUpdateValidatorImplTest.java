package fi.oph.henkilotietomuutospalvelu.service.validators.impl;

import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.oph.henkilotietomuutospalvelu.service.validators.UnknownKoodi;
import fi.vm.sade.oppijanumerorekisteri.dto.*;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class CorrectingHenkiloUpdateValidatorImplTest {
    @InjectMocks
    private CorrectingHenkiloUpdateValidatorImpl validator;

    @Mock
    private KoodistoService koodistoService;

    @Test
    public void testNullValues() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        this.validator.validateAndCorrectErrors(henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto)
                .extracting(HenkiloForceUpdateDto::getHuoltajat, HenkiloForceUpdateDto::getKotikunta, HenkiloUpdateDto::getAidinkieli, HenkiloUpdateDto::getKansalaisuus)
                .containsExactly(null, null, null, null);
    }

    @Test
    public void testHuoltajaNullValues() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(new HuoltajaCreateDto()));
        this.validator.validateAndCorrectErrors(henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHuoltajuustyyppiKoodi, HuoltajaCreateDto::getKansalaisuusKoodi)
                .containsExactly(Tuple.tuple(null, null));
    }

    @Test
    public void testAllValuesInvalid() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setKotikunta("invalid");
        KansalaisuusDto invalidKansalaisuus = new KansalaisuusDto();
        invalidKansalaisuus.setKansalaisuusKoodi("invalid");
        henkiloForceUpdateDto.setKansalaisuus(Collections.singleton(invalidKansalaisuus));
        KielisyysDto invalidKielisyys = new KielisyysDto();
        invalidKielisyys.setKieliKoodi("invalid");
        henkiloForceUpdateDto.setAidinkieli(invalidKielisyys);
        HuoltajaCreateDto huoltajaCreateDto = new HuoltajaCreateDto();
        huoltajaCreateDto.setHuoltajuustyyppiKoodi("invalid");
        huoltajaCreateDto.setKansalaisuusKoodi(Collections.singleton("invalid"));
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));

        this.validator.validateAndCorrectErrors(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto)
                .extracting(HenkiloForceUpdateDto::getKotikunta, updateDto -> updateDto.getAidinkieli().getKieliKoodi())
                .containsExactly(UnknownKoodi.KUNTAKOODI_TUNTEMATON.getKoodi(), UnknownKoodi.KIELIKOODI_TUNTEMATON.getKoodi());
        assertThat(henkiloForceUpdateDto.getKansalaisuus())
                .extracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactly(UnknownKoodi.KANSALAISUUSKOODI_TUNTEMATON.getKoodi());
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHuoltajuustyyppiKoodi,
                        HuoltajaCreateDto::getKansalaisuusKoodi)
                .containsExactly(Tuple.tuple(UnknownKoodi.HUOLTAJUUSTYYPPI_TUNTEMATON.getKoodi(),
                        Collections.singleton(UnknownKoodi.KANSALAISUUSKOODI_TUNTEMATON.getKoodi())));
    }

    @Test
    public void testAllValuesValid() {
        given(this.koodistoService.list(eq(Koodisto.HUOLTAJUUSTYYPPI)))
                .willReturn(Collections.singletonList(KoodiDto.builder().koodiArvo("validHuoltajuustyyppi").build()));
        given(this.koodistoService.list(eq(Koodisto.KUNTA)))
                .willReturn(Collections.singletonList(KoodiDto.builder().koodiArvo("validKunta").build()));
        given(this.koodistoService.list(eq(Koodisto.MAAT_JA_VALTIOT_2)))
                .willReturn(Collections.singletonList(KoodiDto.builder().koodiArvo("validMaa").build()));
        given(this.koodistoService.list(eq(Koodisto.KIELI)))
                .willReturn(Collections.singletonList(KoodiDto.builder().koodiArvo("validKieli").build()));
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setKotikunta("validKunta");
        KansalaisuusDto invalidKansalaisuus = new KansalaisuusDto();
        invalidKansalaisuus.setKansalaisuusKoodi("validMaa");
        henkiloForceUpdateDto.setKansalaisuus(Collections.singleton(invalidKansalaisuus));
        KielisyysDto invalidKielisyys = new KielisyysDto();
        invalidKielisyys.setKieliKoodi("validKieli");
        henkiloForceUpdateDto.setAidinkieli(invalidKielisyys);
        HuoltajaCreateDto huoltajaCreateDto = new HuoltajaCreateDto();
        huoltajaCreateDto.setHuoltajuustyyppiKoodi("validHuoltajuustyyppi");
        huoltajaCreateDto.setKansalaisuusKoodi(Collections.singleton("validMaa"));
        henkiloForceUpdateDto.setHuoltajat(Collections.singleton(huoltajaCreateDto));

        this.validator.validateAndCorrectErrors(henkiloForceUpdateDto);

        assertThat(henkiloForceUpdateDto)
                .extracting(HenkiloForceUpdateDto::getKotikunta, updateDto -> updateDto.getAidinkieli().getKieliKoodi())
                .containsExactly("validKunta", "validKieli");
        assertThat(henkiloForceUpdateDto.getKansalaisuus())
                .extracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactly("validMaa");
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHuoltajuustyyppiKoodi, HuoltajaCreateDto::getKansalaisuusKoodi)
                .containsExactly(Tuple.tuple("validHuoltajuustyyppi", Collections.singleton("validMaa")));
    }

}
