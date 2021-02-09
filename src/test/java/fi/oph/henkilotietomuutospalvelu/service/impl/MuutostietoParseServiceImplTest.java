package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.MuutosType;
import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoParseService;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;

public class MuutostietoParseServiceImplTest {

    private final MuutostietoParseService service = new MuutostietoParseServiceImpl();

    @Test
    @Ignore("Ajetaan testirivien generoimiseksi.")
    public void generateMuutostieto() {
        LocalDate eilinen = LocalDate.now().minus(1, ChronoUnit.DAYS);
        Tietoryhma lisays = Huoltaja.builder()
                .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                .muutostapa(Muutostapa.LISATTY)
                .hetu("110981-076P")
                .laji("3")
                .startDate(eilinen)
                .endDate(eilinen.plus(18, ChronoUnit.YEARS))
                .rooli("2")
                .oikeudet(Collections.emptySet())
                .build();
        Tietoryhma muutos = Huoltaja.builder()
                .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                .muutostapa(Muutostapa.MUUTETTU)
                .hetu("110981-076P")
                .laji("3")
                .startDate(LocalDate.of(2000, 1, 1))
                .endDate(eilinen)
                .rooli("2")
                .oikeudet(Collections.emptySet())
                .build();
        MuutostietoDto dto = MuutostietoDto.builder()
                .hetu("140112A645R")
                .muutosType(MuutosType.VANHA)
                .rekisterointipaiva(eilinen)
                .rivi(1)
                .role("504")
                .tapahtuma("ABC")
                .tiedostoNimi("GENEROITUDATA")
                .tietoryhmat(Arrays.asList(lisays, muutos))
                .build();
        String serialized = service.serializeMuutostietoDto(dto);
        System.out.println(serialized);
        /*
        - muutostiedosto kaivetaan kontin import-hakemistosta
        - serialisoidut muutostiedot riveittäin tiedostoon
        - tiedoston lopussa ei saa olla tyhjää riviä
        - tiedoston nimeämiskäytäntö: ks. FileServiceImpl.parsePartNumber()
         */
    }
}
