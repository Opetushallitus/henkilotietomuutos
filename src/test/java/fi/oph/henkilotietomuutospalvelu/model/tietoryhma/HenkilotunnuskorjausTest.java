package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HenkilotunnuskorjausTest {
    @Test
    public void hetuNotChanged() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjaus = Henkilotunnuskorjaus.builder()
                .hetu("hetu1")
                .active(true)
                .build();

        henkilotunnuskorjaus.setMuutostapa(Muutostapa.LISATTY);
        henkilotunnuskorjaus.updateHenkilo(context, henkilo);
        assertThat(henkilo.getHetu()).isNull();

        henkilotunnuskorjaus.setMuutostapa(Muutostapa.LISATIETO);
        henkilotunnuskorjaus.updateHenkilo(context, henkilo);
        assertThat(henkilo.getHetu()).isNull();

        henkilotunnuskorjaus.setMuutostapa(Muutostapa.MUUTETTU);
        henkilotunnuskorjaus.updateHenkilo(context, henkilo);
        assertThat(henkilo.getHetu()).isNull();
    }

    @Test
    public void muutostietoNotActive() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjaus = Henkilotunnuskorjaus.builder()
                .hetu("hetu2")
                .active(false)
                .build();

        henkilotunnuskorjaus.setMuutostapa(Muutostapa.LISATTY);
        henkilotunnuskorjaus.updateHenkilo(context, henkilo);
        assertThat(henkilo.getHetu()).isNull();

        henkilotunnuskorjaus.setMuutostapa(Muutostapa.LISATIETO);
        henkilotunnuskorjaus.updateHenkilo(context, henkilo);
        assertThat(henkilo.getHetu()).isNull();

        henkilotunnuskorjaus.setMuutostapa(Muutostapa.MUUTETTU);
        henkilotunnuskorjaus.updateHenkilo(context, henkilo);
        assertThat(henkilo.getHetu()).isNull();
    }

    @Test
    public void hetuLisattyVanhaMuutettuFalse() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjausLisatty = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.LISATTY)
                .hetu("hetu2")
                .active(true)
                .build();

        Henkilotunnuskorjaus henkilotunnuskorjausMuutettu = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("hetu1")
                .active(false)
                .build();

        henkilotunnuskorjausLisatty.updateHenkilo(context, henkilo);
        henkilotunnuskorjausMuutettu.updateHenkilo(context, henkilo);

        assertThat(henkilo.getHetu()).isEqualTo("hetu2");
    }

    @Test
    public void uusiAktiivinenHetuLisattyJaVanhaMuutettuEpaAktiiviseksi() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjausMuutettu = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("hetu1")
                .active(false)
                .build();

        Henkilotunnuskorjaus henkilotunnuskorjausLisatty = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.LISATTY)
                .hetu("hetu2")
                .active(true)
                .build();

        henkilotunnuskorjausMuutettu.updateHenkilo(context, henkilo);
        henkilotunnuskorjausLisatty.updateHenkilo(context, henkilo);

        assertThat(henkilo.getHetu()).isEqualTo("hetu2");
    }

    @Test
    public void vanhaHetuEiVastaaLisatiedonAktiivista() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjausMuutettu = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("hetu1")
                .active(false)
                .build();

        Henkilotunnuskorjaus henkilotunnuskorjausLisatieto = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .hetu("hetu2")
                .active(true)
                .build();

        henkilotunnuskorjausMuutettu.updateHenkilo(context, henkilo);
        henkilotunnuskorjausLisatieto.updateHenkilo(context, henkilo);

        assertThat(henkilo.getHetu()).isEqualTo("hetu2");
    }

    @Test
    public void hetuEiPaivityJosValmiiksiAktiivisenaLisatiodossa() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjausLisatieto = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .hetu("hetu1")
                .active(true)
                .build();

        Henkilotunnuskorjaus henkilotunnuskorjausMuutettu = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("hetu2")
                .active(false)
                .build();

        henkilotunnuskorjausLisatieto.updateHenkilo(context, henkilo);
        henkilotunnuskorjausMuutettu.updateHenkilo(context, henkilo);

        assertThat(henkilo.getHetu()).isNull();
    }

    @Test
    public void hetuMuutettuJaLisatietonaVanhaEpaAktiivinen() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjausMuutettu = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("hetu2")
                .active(true)
                .build();

        Henkilotunnuskorjaus henkilotunnuskorjausLisatieto = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .hetu("hetu1")
                .active(false)
                .build();

        henkilotunnuskorjausMuutettu.updateHenkilo(context, henkilo);
        henkilotunnuskorjausLisatieto.updateHenkilo(context, henkilo);

        assertThat(henkilo.getHetu()).isEqualTo("hetu2");
    }

    @Test
    public void lisatietonaVanhaEpaAktiivinenJaHetuMuutettu() {
        HenkiloForceUpdateDto henkilo = new HenkiloForceUpdateDto();
        HenkiloForceReadDto currentHenkilo = HenkiloForceReadDto.builder()
                .hetu("hetu1")
                .build();
        Tietoryhma.Context context = new TestTietoryhmaContextImpl(currentHenkilo);
        Henkilotunnuskorjaus henkilotunnuskorjausLisatieto = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.LISATIETO)
                .hetu("hetu1")
                .active(false)
                .build();

        Henkilotunnuskorjaus henkilotunnuskorjausMuutettu = Henkilotunnuskorjaus.builder()
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("hetu2")
                .active(true)
                .build();

        henkilotunnuskorjausLisatieto.updateHenkilo(context, henkilo);
        henkilotunnuskorjausMuutettu.updateHenkilo(context, henkilo);

        assertThat(henkilo.getHetu()).isEqualTo("hetu2");
    }
}