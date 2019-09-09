package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoAlkupera;
import fi.oph.henkilotietomuutospalvelu.dto.type.KoodistoYhteystietoTyyppi;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceReadDto;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class YhteystietoTietoryhmaTest {

    private YhteystietoTietoryhma tietoryhma;

    @Before
    public void setup() {
        tietoryhma = spy(YhteystietoTietoryhma.class);
    }

    @Test
    public void updateHenkiloLisays() {
        when(tietoryhma.getTyyppi()).thenReturn(KoodistoYhteystietoTyyppi.KOTIMAINEN);
        HenkiloForceReadDto current = new HenkiloForceReadDto();
        TestTietoryhmaContextImpl context = new TestTietoryhmaContextImpl(current);
        HenkiloForceUpdateDto updated = new HenkiloForceUpdateDto();
        updated.setYhteystiedotRyhma(new HashSet<>());
        YhteystiedotRyhmaDto yhteystietoryhma = new YhteystiedotRyhmaDto();
        yhteystietoryhma.setRyhmaAlkuperaTieto(KoodistoYhteystietoAlkupera.VTJ.getKoodi());
        yhteystietoryhma.setRyhmaKuvaus(KoodistoYhteystietoTyyppi.ULKOMAINEN.getKoodi());
        updated.getYhteystiedotRyhma().add(yhteystietoryhma);

        tietoryhma.setMuutostapa(Muutostapa.LISATTY);
        tietoryhma.updateHenkilo(context, updated);

        ArgumentCaptor<YhteystiedotRyhmaDto> captor = ArgumentCaptor.forClass(YhteystiedotRyhmaDto.class);
        verify(tietoryhma).updateYhteystietoryhma(eq(context), captor.capture());
        assertThat(captor.getValue())
                .isNotSameAs(yhteystietoryhma)
                .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactly(KoodistoYhteystietoTyyppi.KOTIMAINEN.getKoodi());
        assertThat(updated.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactlyInAnyOrder(KoodistoYhteystietoTyyppi.KOTIMAINEN.getKoodi(), KoodistoYhteystietoTyyppi.ULKOMAINEN.getKoodi());
    }

    @Test
    public void updateHenkiloMuokkaus() {
        when(tietoryhma.getTyyppi()).thenReturn(KoodistoYhteystietoTyyppi.KOTIMAINEN);
        HenkiloForceReadDto current = new HenkiloForceReadDto();
        TestTietoryhmaContextImpl context = new TestTietoryhmaContextImpl(current);
        HenkiloForceUpdateDto updated = new HenkiloForceUpdateDto();
        updated.setYhteystiedotRyhma(new HashSet<>());
        YhteystiedotRyhmaDto yhteystietoryhma1 = new YhteystiedotRyhmaDto();
        yhteystietoryhma1.setRyhmaAlkuperaTieto(KoodistoYhteystietoAlkupera.VTJ.getKoodi());
        yhteystietoryhma1.setRyhmaKuvaus(KoodistoYhteystietoTyyppi.KOTIMAINEN.getKoodi());
        updated.getYhteystiedotRyhma().add(yhteystietoryhma1);
        YhteystiedotRyhmaDto yhteystietoryhma2 = new YhteystiedotRyhmaDto();
        yhteystietoryhma2.setRyhmaAlkuperaTieto(KoodistoYhteystietoAlkupera.VTJ.getKoodi());
        yhteystietoryhma2.setRyhmaKuvaus(KoodistoYhteystietoTyyppi.ULKOMAINEN.getKoodi());
        updated.getYhteystiedotRyhma().add(yhteystietoryhma2);

        tietoryhma.setMuutostapa(Muutostapa.MUUTETTU);
        tietoryhma.updateHenkilo(context, updated);

        ArgumentCaptor<YhteystiedotRyhmaDto> captor = ArgumentCaptor.forClass(YhteystiedotRyhmaDto.class);
        verify(tietoryhma).updateYhteystietoryhma(eq(context), captor.capture());
        assertThat(captor.getValue())
                .isNotSameAs(yhteystietoryhma1)
                .isNotSameAs(yhteystietoryhma2)
                .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactly(KoodistoYhteystietoTyyppi.KOTIMAINEN.getKoodi());
        assertThat(updated.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactlyInAnyOrder(KoodistoYhteystietoTyyppi.KOTIMAINEN.getKoodi(), KoodistoYhteystietoTyyppi.ULKOMAINEN.getKoodi());
    }

    @Test
    public void updateHenkiloPoisto() {
        when(tietoryhma.getTyyppi()).thenReturn(KoodistoYhteystietoTyyppi.KOTIMAINEN);
        HenkiloForceReadDto current = new HenkiloForceReadDto();
        TestTietoryhmaContextImpl context = new TestTietoryhmaContextImpl(current);
        HenkiloForceUpdateDto updated = new HenkiloForceUpdateDto();
        updated.setYhteystiedotRyhma(new HashSet<>());
        YhteystiedotRyhmaDto yhteystietoryhma1 = new YhteystiedotRyhmaDto();
        yhteystietoryhma1.setRyhmaAlkuperaTieto(KoodistoYhteystietoAlkupera.VTJ.getKoodi());
        yhteystietoryhma1.setRyhmaKuvaus(KoodistoYhteystietoTyyppi.KOTIMAINEN.getKoodi());
        updated.getYhteystiedotRyhma().add(yhteystietoryhma1);
        YhteystiedotRyhmaDto yhteystietoryhma2 = new YhteystiedotRyhmaDto();
        yhteystietoryhma2.setRyhmaAlkuperaTieto(KoodistoYhteystietoAlkupera.VTJ.getKoodi());
        yhteystietoryhma2.setRyhmaKuvaus(KoodistoYhteystietoTyyppi.ULKOMAINEN.getKoodi());
        updated.getYhteystiedotRyhma().add(yhteystietoryhma2);

        tietoryhma.setMuutostapa(Muutostapa.POISTETTU);
        tietoryhma.updateHenkilo(context, updated);

        verify(tietoryhma, never()).updateYhteystietoryhma(any(), any());
        assertThat(updated.getYhteystiedotRyhma())
                .extracting(YhteystiedotRyhmaDto::getRyhmaKuvaus)
                .containsExactly(KoodistoYhteystietoTyyppi.ULKOMAINEN.getKoodi());
    }

}
