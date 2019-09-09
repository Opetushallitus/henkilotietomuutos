package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TurvakieltoTest {

    @Test
    public void lisatty() {
        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        TestTietoryhmaContextImpl context = new TestTietoryhmaContextImpl(readDto, LocalDate.of(2019, 1, 25));
        Turvakielto turvakielto = new Turvakielto();
        turvakielto.setMuutostapa(Muutostapa.LISATTY);
        turvakielto.setEndDate(LocalDate.of(2020, 1, 25));

        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        turvakielto.updateHenkilo(context, updateDto);

        assertThat(updateDto).returns(true, HenkiloForceUpdateDto::getTurvakielto);
    }

    @Test
    public void poistettu() {
        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        TestTietoryhmaContextImpl context = new TestTietoryhmaContextImpl(readDto, LocalDate.of(2019, 1, 25));
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();
        Turvakielto turvakielto = new Turvakielto();
        turvakielto.setMuutostapa(Muutostapa.POISTETTU);
        turvakielto.setEndDate(LocalDate.of(2019, 1, 25));

        turvakielto.updateHenkilo(context, updateDto);

        assertThat(updateDto).returns(false, HenkiloForceUpdateDto::getTurvakielto);
    }

    @Test
    public void uudelleenkasittely() {
        HenkiloForceReadDto readDto = new HenkiloForceReadDto();
        readDto.setTurvakielto(false);
        TestTietoryhmaContextImpl context = new TestTietoryhmaContextImpl(readDto, LocalDate.of(2019, 1, 25));
        HenkiloForceUpdateDto updateDto = new HenkiloForceUpdateDto();

        Turvakielto turvakielto1 = new Turvakielto();
        turvakielto1.setMuutostapa(Muutostapa.LISATTY);
        turvakielto1.setEndDate(null);
        turvakielto1.updateHenkilo(context, updateDto);
        assertThat(updateDto).returns(true, HenkiloForceUpdateDto::getTurvakielto);

        Turvakielto turvakielto2 = new Turvakielto();
        turvakielto2.setMuutostapa(Muutostapa.POISTETTU);
        turvakielto2.setEndDate(LocalDate.of(2018, 7, 25));
        turvakielto2.updateHenkilo(context, updateDto);
        assertThat(updateDto).returns(false, HenkiloForceUpdateDto::getTurvakielto);
    }

}
