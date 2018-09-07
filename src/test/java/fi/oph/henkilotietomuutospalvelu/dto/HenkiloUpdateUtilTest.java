package fi.oph.henkilotietomuutospalvelu.dto;

import fi.oph.henkilotietomuutospalvelu.service.build.HenkiloUpdateUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HenkiloUpdateUtilTest {

    @Test
    public void isValidKutsumanimiWithColon() {
        String etunimet = "Lauri-Matti Johannes";
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "lauri")).isTrue();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "lauri-matti")).isTrue();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "johannes")).isTrue();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "hannes")).isFalse();
    }

    @Test
    public void emptyKutsumanimi() {
        String etunimet = "Lauri-Matti Johannes";
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "")).isFalse();
    }

    @Test
    public void isValidKutsumanimiWithNonAlphabet() {
        String etunimet = "As'ko";
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "As'ko")).isTrue();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "as'ko")).isTrue();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "as")).isFalse();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "ko")).isFalse();
    }

    @Test
    public void isValidKutsumanimiWithSingleEtunimi() {
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi("Ilona", "Ilona")).isTrue();
    }

    @Test
    public void isValidKutsumanimiWithDifferentWhitespaces() {
        String etunimet = "  Veikko    Severi \n Olli";
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "Veikko")).isTrue();
        assertThat(HenkiloUpdateUtil.isValidKutsumanimi(etunimet, "Olli")).isTrue();
    }

}
