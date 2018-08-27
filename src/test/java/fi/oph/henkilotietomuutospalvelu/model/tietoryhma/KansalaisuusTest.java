package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import com.google.common.collect.Sets;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KansalaisuusDto;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class KansalaisuusTest {

    @Test
    public void validAndUpToDateKansalaisuusIsSet() {
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        Kansalaisuus kansalaisuus = Kansalaisuus.builder()
                .code("246")
                .endDate(LocalDate.now().plusDays(10))
                .valid(true)
                .muutostapa(Muutostapa.LISATTY)
                .build();
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), updateDto);
        assertThat(updateDto.getKansalaisuus())
                .flatExtracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactly("246");
    }

    @Test
    public void inValidAndUpToDateKansalaisuusIsNotSet() {
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        Kansalaisuus kansalaisuus = Kansalaisuus.builder()
                .code("246")
                .endDate(LocalDate.now().plusDays(10))
                .valid(false)
                .muutostapa(Muutostapa.LISATTY)
                .build();
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), updateDto);
        assertThat(updateDto.getKansalaisuus()).isNull();
    }

    @Test
    public void validAndNotUpToDateKansalaisuusIsNotSet() {
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        Kansalaisuus kansalaisuus = Kansalaisuus.builder()
                .code("246")
                .endDate(LocalDate.now().minusDays(10))
                .valid(true)
                .muutostapa(Muutostapa.LISATTY)
                .build();
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), updateDto);
        assertThat(updateDto.getKansalaisuus()).isNull();
    }

    @Test
    public void newKansalaisuusIsAddedToOldList() {
        KansalaisuusDto kansalaisuusDto = new KansalaisuusDto();
        kansalaisuusDto.setKansalaisuusKoodi("100");
        HenkiloDto henkiloDto = HenkiloDto.builder()
                .kansalaisuus(Sets.newHashSet(kansalaisuusDto))
                .build();
        HenkiloForceUpdateDto updateDto =  new HenkiloForceUpdateDto();
        Kansalaisuus kansalaisuus = Kansalaisuus.builder()
                .code("246")
                .endDate(LocalDate.now().plusDays(10))
                .valid(true)
                .muutostapa(Muutostapa.MUUTETTU)
                .build();
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(henkiloDto), updateDto);
        assertThat(updateDto.getKansalaisuus())
                .flatExtracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactlyInAnyOrder("100", "246");
    }

    @Test
    public void invalidKansalaisuusPreservsOldList() {
        KansalaisuusDto kansalaisuusDto = new KansalaisuusDto();
        kansalaisuusDto.setKansalaisuusKoodi("100");
        HenkiloDto henkiloDto = HenkiloDto.builder()
                .kansalaisuus(Sets.newHashSet(kansalaisuusDto))
                .build();
        HenkiloForceUpdateDto updateDto =  new HenkiloForceUpdateDto();
        Kansalaisuus kansalaisuus = Kansalaisuus.builder()
                .code("246")
                .endDate(LocalDate.now().plusDays(10))
                .valid(false)
                .muutostapa(Muutostapa.MUUTETTU)
                .build();
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(henkiloDto), updateDto);
        assertThat(updateDto.getKansalaisuus()).isNull();
    }

}