package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class KutsumanimiTest {
    private HenkiloForceUpdateDto henkiloForceUpdateDto;

    @Before
    public void setup() {
        this.henkiloForceUpdateDto = new HenkiloForceUpdateDto();
    }

    @Test
    public void kutsumanimiIsSet() {
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("etu nimi")
                .kutsumanimi("etu")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(henkiloDto);

        Kutsumanimi kutsumanimi = Kutsumanimi.builder()
                .endDate(null)
                .startDate(LocalDate.now().minusDays(1))
                .name("nimi")
                .build();
        kutsumanimi.updateHenkiloInternal(context, this.henkiloForceUpdateDto);
        assertThat(this.henkiloForceUpdateDto.getKutsumanimi()).isEqualTo("nimi");
    }

    @Test
    public void oldKutsumanimiIsNotSet() {
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("etu nimi")
                .kutsumanimi("etu")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(henkiloDto);

        Kutsumanimi kutsumanimi = Kutsumanimi.builder()
                .endDate(LocalDate.now().minusDays(1))
                .name("nimi")
                .build();
        kutsumanimi.updateHenkiloInternal(context, this.henkiloForceUpdateDto);
        assertThat(this.henkiloForceUpdateDto.getKutsumanimi()).isNull();
    }

    @Test
    public void dualPartkutsumanimiIsSet() {
        HenkiloForceReadDto henkiloDto = HenkiloForceReadDto.builder()
                .etunimet("etu-toka nimi")
                .kutsumanimi("nimi")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(henkiloDto);

        Kutsumanimi kutsumanimi = Kutsumanimi.builder()
                .endDate(null)
                .startDate(LocalDate.now().minusDays(1))
                .name("etu-toka")
                .build();
        kutsumanimi.updateHenkiloInternal(context, this.henkiloForceUpdateDto);
        assertThat(this.henkiloForceUpdateDto.getKutsumanimi()).isEqualTo("etu-toka");
    }


}