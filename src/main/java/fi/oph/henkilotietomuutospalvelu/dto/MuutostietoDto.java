package fi.oph.henkilotietomuutospalvelu.dto;

import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.dto.type.MuutosType;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class MuutostietoDto {

    /**
     * Määrittelee muutoksen kohteena olevan yksilön henkilötunnuksen.
     * Yhdeksän merkkiä muodossa PPKKVVVV[+/-/A]XXXX.
     */
    private String hetu;

    /**
     * Määrittelee tapahtuman, josta muutos on lähtöisin.
     * Tapahtuma on aina kolmekirjaiminen ja vastaa lomaketunnusta.
     */
    private String tapahtuma;

    /**
     * Ilmoittaa päivän, jona muutos on kirjattu VTJ:hin.
     */
    private LocalDate rekisterointipaiva;

    /**
     * Kertoo onko kyseessä uuden yksilön lisäys VTJ:ään (esim. syntymä, uutena rekisteröinti, kuntaan tai seurakuntaan muutto).
     * Uuden yksilön lisäys ilmoitetaan arvolla UUSI (1), Vanhan yksilön muokkaus arvolla VANHA (0).
     * Mikäli muutostieto jatkaa aikaisempaa muutostietoa, ilmoitetaan se arvolla JATKETTU (J).
     */
    @Builder.Default
    private MuutosType muutosType = MuutosType.TYHJA;

    /**
     * Selventää muutoksen kohteena olleen yksilön asemaa tapahtumassa (esim. äiti, isä, lapsi).
     */
    private String role;

    /**
     * Sisältää tietueeseen kuuluvat tietoryhmät.
     */
    @Builder.Default
    private List<Tietoryhma> tietoryhmat = new ArrayList<>();

    private int rivi;

    private String tiedostoNimi;

}
