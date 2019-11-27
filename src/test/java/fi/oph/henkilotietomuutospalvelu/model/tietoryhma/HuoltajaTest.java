package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class HuoltajaTest {
    @Test
    public void useaValidHetullinenHuoltaja() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(new HashSet<>());
        Huoltaja huoltaja1 = Huoltaja.builder()
                .hetu("hetu1")
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        Huoltaja huoltaja2 = Huoltaja.builder()
                .hetu("hetu2")
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactlyInAnyOrder("hetu1", "hetu2");
    }

    @Test
    public void useaValidHetutonHuoltaja() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(new HashSet<>());
        Huoltaja huoltaja1 = Huoltaja.builder()
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet1")
                        .lastname("Sukunimi1")
                        .build())
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        Huoltaja huoltaja2 = Huoltaja.builder()
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet2")
                        .lastname("Sukunimi2")
                        .build())
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi)
                .containsExactlyInAnyOrder(Tuple.tuple("Etunimet1", "Sukunimi1"), Tuple.tuple("Etunimet2", "Sukunimi2"));
    }

    @Test
    public void useaValidiHuoltajaSamallaHetullaKuuluuYhdistya() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(new HashSet<>());
        Huoltaja huoltaja1 = Huoltaja.builder()
                .hetu("hetu")
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactly("hetu");
        Huoltaja huoltaja2 = Huoltaja.builder()
                .hetu("hetu")
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactly("hetu");
    }

    @Test
    public void useaHetututonHuoltajaSamallaNimellaKuuluuYhdistya() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(new HashSet<>());
        Huoltaja huoltaja1 = Huoltaja.builder()
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet")
                        .lastname("Sukunimi")
                        .nationality("00")
                        .build())
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi)
                .containsExactly(Tuple.tuple("Etunimet", "Sukunimi"));
        Huoltaja huoltaja2 = Huoltaja.builder()
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet")
                        .lastname("Sukunimi")
                        .nationality("01")
                        .build())
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloForceReadDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi, HuoltajaCreateDto::getKansalaisuusKoodi)
                .containsExactly(Tuple.tuple("Etunimet", "Sukunimi", Collections.singleton("01")));
    }

    @Test
    public void muutettuToimii() {
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(new HenkiloForceReadDto());
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        updateDto.setHuoltajat(Stream.of("100999-919W")
                .map(hetu -> new HuoltajaCreateDto() {{ setHetu(hetu); }})
                .collect(toSet()));

        Huoltaja.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("100999-919W")
                .laji("02")
                .build()
                .updateHenkilo(context, updateDto);

        assertThat(updateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactly("100999-919W");
    }

    @Test
    public void korjattuToimii() {
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(new HenkiloForceReadDto());
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        updateDto.setHuoltajat(Stream.of("100999-919W")
                .map(hetu -> new HuoltajaCreateDto() {{ setHetu(hetu); }})
                .collect(toSet()));

        Huoltaja.builder()
                .muutostapa(Muutostapa.KORJATTU)
                .hetu("100999-919W")
                .laji("02")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.KORJATTAVAA)
                .hetu("100999-919W")
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);

        assertThat(updateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactly("100999-919W");
    }

    @Test
    public void poistoToimii() {
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(new HenkiloForceReadDto());
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        updateDto.setHuoltajat(Stream.of("100999-919W")
                .map(hetu -> new HuoltajaCreateDto() {{ setHetu(hetu); }})
                .collect(toSet()));

        Huoltaja.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .hetu("100999-919W")
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);

        assertThat(updateDto.getHuoltajat()).isEmpty();
    }

    @Test
    public void vanhentunutPoistoToimii() {
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(new HenkiloForceReadDto(), LocalDate.of(2019, 9, 10));
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        updateDto.setHuoltajat(Stream.of("100999-919W")
                .map(hetu -> new HuoltajaCreateDto() {{ setHetu(hetu); }})
                .collect(toSet()));

        Huoltaja.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .startDate(LocalDate.of(2019, 1, 1))
                .endDate(LocalDate.of(2019, 2, 1))
                .hetu("100999-919W")
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);

        assertThat(updateDto.getHuoltajat()).isEmpty();
    }

    @Test
    public void hetullinenUudelleenkasittelyToimii() {
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(new HenkiloForceReadDto());
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        updateDto.setHuoltajat(Stream.of("100999-919W")
                .map(hetu -> new HuoltajaCreateDto() {{ setHetu(hetu); }})
                .collect(toSet()));

        Huoltaja.builder()
                .muutostapa(Muutostapa.LISATTY)
                .hetu("100999-919W")
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.LISATTY)
                .hetu("100999-9541")
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("100999-9541")
                .laji("02")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .hetu("100999-919W")
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);

        assertThat(updateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactly("100999-9541");
    }

    @Test
    public void hetutonUudelleenkasittelyToimii() {
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(new HenkiloForceReadDto());
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        HuoltajaCreateDto huoltaja1 = new HuoltajaCreateDto();
        huoltaja1.setEtunimet("Etunimet2");
        huoltaja1.setSukunimi("Sukunimi1");
        updateDto.setHuoltajat(Stream.of(huoltaja1).collect(toSet()));

        Huoltaja.builder()
                .muutostapa(Muutostapa.LISATTY)
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet1")
                        .lastname("Sukunimi1")
                        .build())
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.LISATTY)
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet2")
                        .lastname("Sukunimi1")
                        .build())
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet1")
                        .lastname("Sukunimi1")
                        .build())
                .laji("02")
                .build()
                .updateHenkilo(context, updateDto);
        Huoltaja.builder()
                .muutostapa(Muutostapa.POISTETTU)
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet2")
                        .lastname("Sukunimi1")
                        .build())
                .laji("01")
                .build()
                .updateHenkilo(context, updateDto);

        assertThat(updateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi)
                .containsExactly(tuple("Etunimet1", "Sukunimi1"));
    }
}
