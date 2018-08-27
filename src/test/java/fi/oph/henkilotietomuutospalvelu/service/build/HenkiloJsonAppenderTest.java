package fi.oph.henkilotietomuutospalvelu.service.build;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HenkiloJsonAppenderTest {

    @Test
    public void testAidinkieliJson() {
        Aidinkieli aidinkieli = Aidinkieli.builder()
                .languageCode("fi")
                .build();
        String testJson = "\"aidinkieli\":{\"kieliKoodi\":\"fi\",\"kieliTyyppi\":null}";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendAidinkieli(aidinkieli);
        Assert.assertEquals(testJson, builder.toString());
    }

    @Test
    public void testHetuJson() {
        Henkilotunnuskorjaus hetu = Henkilotunnuskorjaus.builder()
                .hetu("131052-308T")
                .build();
        String testJson = "\"hetu\":\"131052-308T\"";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendHetuChange(hetu);
        Assert.assertEquals(testJson, builder.toString());
    }

    @Test
    public void testKansalaisuudetJson() {
        List<Kansalaisuus> kansalaisuudet = Arrays.asList(
                Kansalaisuus.builder()
                        .code("246")
                        .build()
                ,
                Kansalaisuus.builder()
                        .code("752")
                        .build());
        String testJson = "\"kansalaisuus\":[{\"kansalaisuusKoodi\":\"246\"},{\"kansalaisuusKoodi\":\"752\"}]";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendKansalaisuudet(kansalaisuudet);
        Assert.assertEquals(testJson, builder.toString());
    }

    @Test
    public void testKuolinpaivaJson() {
        Kuolinpaiva kuolinpaiva = Kuolinpaiva.builder()
                .dateOfDeath(LocalDate.of(2017, 2, 1))
                .build();
        String testJson = "\"kuolinpaiva\":\"2017-02-01\"";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendKuolinpaiva(kuolinpaiva);
        Assert.assertEquals(testJson, builder.toString());
    }

    @Test
    public void testEtunimiNameChangeJson() {
        HenkiloNameChange etunimi = HenkiloNameChange.builder()
                .name("Kalle Jaakoppi")
                .nameType(NameType.ETUNIMI)
                .build();
        String testJson = "\"etunimet\":\"Kalle Jaakoppi\"";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendNameChange(etunimi);
        Assert.assertEquals(testJson, builder.toString());
    }

    @Test
    public void testSukunimiNameChangeJson() {
        HenkiloNameChange sukunimi = HenkiloNameChange.builder()
                .name("Kallioinen")
                .nameType(NameType.SUKUNIMI)
                .build();
        String testJson = "\"sukunimi\":\"Kallioinen\"";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendNameChange(sukunimi);
        Assert.assertEquals(testJson, builder.toString());
    }

    @Test
    public void testKutsumanimiNameChangeJson() {
        HenkiloNameChange kutsumanimi = HenkiloNameChange.builder()
                .name("Kalle")
                .nameType(NameType.KUTSUMANIMI)
                .build();
        String testJson = "\"kutsumanimi\":\"Kalle\"";

        HenkiloJsonBuilder builder = HenkiloJsonAppender.appendNameChange(kutsumanimi);
        Assert.assertEquals(testJson, builder.toString());
    }

}
