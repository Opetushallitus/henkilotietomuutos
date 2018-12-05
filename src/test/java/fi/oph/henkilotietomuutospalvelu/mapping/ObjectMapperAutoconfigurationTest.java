package fi.oph.henkilotietomuutospalvelu.mapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.ArrayList;
import java.util.Collections;

import static fi.vm.sade.rajapinnat.vtj.api.YksiloityHenkilo.EntinenNimiTyyppi.TUNTEMATON;
import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMapperAutoconfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class))
                .withPropertyValues("spring.jackson.serialization.write-dates-as-timestamps=true")
                .withPropertyValues("spring.jackson.deserialization.FAIL_ON_UNKNOWN_PROPERTIES=false");

    @Test
    public void objectMapperShouldBeConfiguredProperly() {
        this.contextRunner.run((context) -> {
            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
            assertThat(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isTrue();
            assertThat(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isFalse();
        });
    }

    @Test
    public void yksiloityHenkiloShouldMapCorrectly() {
        this.contextRunner
                .run((context) -> {
                    String json = "{\"passivoitu\":false,\"etunimi\":\"Arpa Noppa\",\"kutsumanimi\":\"\",\"sukunimi\":\"Kuutio\",\"entisetNimet\":[{\"tyyppi\":\"TUNTEMATON\",\"arvo\":\"\",\"sukunimi\":false}],\"hetu\":\"hetu\",\"sukupuoli\":\"2\",\"turvakielto\":false,\"sahkoposti\":\"\",\"aidinkieliKoodi\":\"fi\",\"kansalaisuusKoodit\":[\"246\"],\"osoitteet\":[{\"tyyppi\":\"yhteystietotyyppi4\",\"katuosoiteS\":\"Jokintie 111\",\"katuosoiteR\":\"Jokintie 111\",\"kaupunkiS\":\"JOKUKAUPUNKI\",\"kaupunkiR\":\"JOKUKAUPUNKI\",\"postinumero\":\"00000\",\"maaS\":\"Suomi\",\"maaR\":\"Finland\"},{\"tyyppi\":\"yhteystietotyyppi5\",\"katuosoiteS\":\"\",\"katuosoiteR\":null,\"kaupunkiS\":\"\",\"kaupunkiR\":null,\"postinumero\":null,\"maaS\":\"\",\"maaR\":\"\"}],\"kotikunta\":\"785\",\"huoltajat\":[]}";
                    ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
                    YksiloityHenkilo yksiloityHenkilo = objectMapper.readValue(json, YksiloityHenkilo.class);
                    assertThat(yksiloityHenkilo)
                            .extracting(YksiloityHenkilo::isPassivoitu, YksiloityHenkilo::getHetu, YksiloityHenkilo::getEtunimi, YksiloityHenkilo::getKutsumanimi, YksiloityHenkilo::getSukunimi, YksiloityHenkilo::isTurvakielto, YksiloityHenkilo::getSukupuoli, YksiloityHenkilo::getKotikunta, YksiloityHenkilo::getKansalaisuusKoodit, YksiloityHenkilo::getHuoltajat)
                            .containsExactly(false, "hetu", "Arpa Noppa", "", "Kuutio", false, "2", "785", Collections.singletonList("246"), new ArrayList<>());
                    assertThat(yksiloityHenkilo.getEntisetNimet())
                            .extracting(YksiloityHenkilo.EntinenNimi::getTyyppi, YksiloityHenkilo.EntinenNimi::getArvo)
                            .containsExactly(Tuple.tuple(TUNTEMATON, ""));
                    assertThat(yksiloityHenkilo.getOsoitteet())
                            .extracting(YksiloityHenkilo.OsoiteTieto::getTyyppi, YksiloityHenkilo.OsoiteTieto::getPostinumero, YksiloityHenkilo.OsoiteTieto::getKatuosoiteS, YksiloityHenkilo.OsoiteTieto::getKaupunkiS, YksiloityHenkilo.OsoiteTieto::getMaaS)
                            .containsExactlyInAnyOrder(
                                    Tuple.tuple("yhteystietotyyppi4", "00000", "Jokintie 111", "JOKUKAUPUNKI", "Suomi"),
                                    Tuple.tuple("yhteystietotyyppi5", null, "", "", "")
                            );
                });
    }
}
