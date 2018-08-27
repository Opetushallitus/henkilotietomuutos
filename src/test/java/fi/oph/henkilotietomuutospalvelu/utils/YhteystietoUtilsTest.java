package fi.oph.henkilotietomuutospalvelu.utils;

import fi.vm.sade.oppijanumerorekisteri.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoDto;
import fi.vm.sade.oppijanumerorekisteri.dto.YhteystietoTyyppi;
import java.util.HashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import org.junit.Test;

public class YhteystietoUtilsTest {

    @Test
    public void setYhteystietoArvoLisays() {
        YhteystiedotRyhmaDto yhteystietoryhma = new YhteystiedotRyhmaDto();
        yhteystietoryhma.setYhteystieto(new HashSet<>());
        YhteystietoDto yhteystieto = new YhteystietoDto();
        yhteystieto.setYhteystietoTyyppi(YhteystietoTyyppi.YHTEYSTIETO_KUNTA);
        yhteystieto.setYhteystietoArvo("helsinki");
        yhteystietoryhma.getYhteystieto().add(yhteystieto);

        YhteystietoUtils.setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_MAA, "suomi");

        assertThat(yhteystietoryhma.getYhteystieto())
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_KUNTA, "helsinki"),
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_MAA, "suomi")
                );
    }

    @Test
    public void setYhteystietoArvoMuokkaus() {
        YhteystiedotRyhmaDto yhteystietoryhma = new YhteystiedotRyhmaDto();
        yhteystietoryhma.setYhteystieto(new HashSet<>());
        YhteystietoDto yhteystieto = new YhteystietoDto();
        yhteystieto.setYhteystietoTyyppi(YhteystietoTyyppi.YHTEYSTIETO_MAA);
        yhteystieto.setYhteystietoArvo("ruotsi");
        yhteystietoryhma.getYhteystieto().add(yhteystieto);

        YhteystietoUtils.setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_MAA, "suomi");

        assertThat(yhteystietoryhma.getYhteystieto())
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_MAA, "suomi")
                );
    }

    @Test
    public void setYhteystietoArvoPoisto() {
        YhteystiedotRyhmaDto yhteystietoryhma = new YhteystiedotRyhmaDto();
        yhteystietoryhma.setYhteystieto(new HashSet<>());
        YhteystietoDto yhteystieto = new YhteystietoDto();
        yhteystieto.setYhteystietoTyyppi(YhteystietoTyyppi.YHTEYSTIETO_MAA);
        yhteystieto.setYhteystietoArvo("ruotsi");
        yhteystietoryhma.getYhteystieto().add(yhteystieto);

        YhteystietoUtils.setYhteystietoArvo(yhteystietoryhma, YhteystietoTyyppi.YHTEYSTIETO_MAA, null);

        assertThat(yhteystietoryhma.getYhteystieto())
                .extracting(YhteystietoDto::getYhteystietoTyyppi, YhteystietoDto::getYhteystietoArvo)
                .containsExactlyInAnyOrder(
                        tuple(YhteystietoTyyppi.YHTEYSTIETO_MAA, "")
                );
    }

}
