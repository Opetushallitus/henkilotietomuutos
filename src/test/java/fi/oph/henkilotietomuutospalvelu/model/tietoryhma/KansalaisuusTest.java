package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import com.google.common.collect.Sets;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.KansalaisuusDto;
import org.junit.Test;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
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
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), updateDto);
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
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), updateDto);
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
        kansalaisuus.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), updateDto);
        assertThat(updateDto.getKansalaisuus()).isNull();
    }

    @Test
    public void newKansalaisuusIsAddedToOldList() {
        KansalaisuusDto kansalaisuusDto = new KansalaisuusDto();
        kansalaisuusDto.setKansalaisuusKoodi("100");
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
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
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
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

    @Test
    public void poistoLisaysToimii() {
        LocalDate now = LocalDate.of(2022, 1, 1);

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setKansalaisuus(singleton(KansalaisuusDto.fromKansalaisuusKoodi("kansalaisuus1")));

        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        updateDto.setKansalaisuus(null);

        Kansalaisuus.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .valid(true)
                .code("kansalaisuus1")
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);
        Kansalaisuus.builder()
                .muutostapa(Muutostapa.LISATTY)
                .valid(true)
                .code("kansalaisuus2")
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);

        assertThat(updateDto.getKansalaisuus())
                .extracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactly("kansalaisuus2");
    }

    @Test
    public void uudelleenkasittelyToimii() {
        LocalDate now = LocalDate.of(2022, 1, 1);

        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setKansalaisuus(Stream.of("kansalaisuus2", "kansalaisuus3").map(KansalaisuusDto::fromKansalaisuusKoodi).collect(toSet()));

        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();

        Kansalaisuus.builder()
                .muutostapa(Muutostapa.LISATTY)
                .valid(true)
                .code("kansalaisuus1")
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);
        Kansalaisuus.builder()
                .muutostapa(Muutostapa.LISATTY)
                .valid(true)
                .code("kansalaisuus2")
                .startDate(LocalDate.of(2020,1,1))
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);
        Kansalaisuus.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .valid(true)
                .code("kansalaisuus2")
                .startDate(LocalDate.of(2020,1,1))
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);
        Kansalaisuus.builder()
                .muutostapa(Muutostapa.KORJATTAVAA)
                .valid(true)
                .code("kansalaisuus1")
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);
        Kansalaisuus.builder()
                .muutostapa(Muutostapa.KORJATTU)
                .valid(false)
                .code("kansalaisuus1")
                .endDate(LocalDate.of(2020, 1, 1))
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);
        Kansalaisuus.builder()
                .muutostapa(Muutostapa.LISATTY)
                .valid(true)
                .code("kansalaisuus3")
                .startDate(LocalDate.of(2021,1,1))
                .build()
                .updateHenkilo(new TestTietoryhmaContextImpl(readDto, now), updateDto);

        assertThat(updateDto.getKansalaisuus())
                .extracting(KansalaisuusDto::getKansalaisuusKoodi)
                .containsExactlyInAnyOrder("kansalaisuus2", "kansalaisuus3");
    }

}