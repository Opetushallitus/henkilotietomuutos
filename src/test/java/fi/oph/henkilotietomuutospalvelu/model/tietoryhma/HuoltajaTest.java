package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HuoltajaCreateDto;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class HuoltajaTest {
    @Test
    public void useaValidHetullinenHuoltaja() {
        HenkiloForceUpdateDto henkiloForceUpdateDto = new HenkiloForceUpdateDto();
        henkiloForceUpdateDto.setHuoltajat(new HashSet<>());
        Huoltaja huoltaja1 = Huoltaja.builder()
                .hetu("hetu1")
                .voimassa(true)
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
        Huoltaja huoltaja2 = Huoltaja.builder()
                .hetu("hetu2")
                .voimassa(true)
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
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
                .voimassa(true)
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
        Huoltaja huoltaja2 = Huoltaja.builder()
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet2")
                        .lastname("Sukunimi2")
                        .build())
                .voimassa(true)
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
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
                .voimassa(true)
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getHetu)
                .containsExactly("hetu");
        Huoltaja huoltaja2 = Huoltaja.builder()
                .hetu("hetu")
                .voimassa(true)
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
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
                .voimassa(true)
                .build();
        huoltaja1.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi)
                .containsExactly(Tuple.tuple("Etunimet", "Sukunimi"));
        Huoltaja huoltaja2 = Huoltaja.builder()
                .henkilotunnuksetonHenkilo(HenkilotunnuksetonHenkilo.builder()
                        .firstNames("Etunimet")
                        .lastname("Sukunimi")
                        .nationality("01")
                        .build())
                .voimassa(true)
                .build();
        huoltaja2.updateHenkilo(new TestTietoryhmaContextImpl(new HenkiloDto()), henkiloForceUpdateDto);
        assertThat(henkiloForceUpdateDto.getHuoltajat())
                .extracting(HuoltajaCreateDto::getEtunimet, HuoltajaCreateDto::getSukunimi, HuoltajaCreateDto::getKansalaisuusKoodi)
                .containsExactly(Tuple.tuple("Etunimet", "Sukunimi", Collections.singleton("01")));
    }
}
