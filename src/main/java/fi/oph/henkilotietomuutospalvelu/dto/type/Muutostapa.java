package fi.oph.henkilotietomuutospalvelu.dto.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Muutostapa {
    /**
     * Tietoryhmä, joka sisältää toiseen tietoryhmään liittyviä lisätietoja. Esim. kansalaisuuksiin kohdistuneiden
     * päivitystapahtumien yhteydessä välitetään kaikki muut voimassaolevat kansalaisuudet muutosattribuutilla 0.
     */
    LISATIETO(0L),

    /**
     * Lisätään yksilölle tietoryhmä VTJ:ssä tai yksilö on asiakkaalle uusi.
     * Uuden yksilön lisäyksessä kaikki tietoryhmät välitetään muutosattribuutilla 1 ja lisätiedot
     * muutosattribuutilla 0.
     */
    LISATTY(1L),

    /**
     * Tietoryhmä muutoksen jälkeen.
     * Kaikki muutokset välitetään pääsääntöisesti vain muutosattribuutilla ’Muutettu’ eli annetaan
     * tapahtumassa kirjattu uusi tieto.
     */
    MUUTETTU(3L),

    /**
     * Poistetaan yksilöltä tieto VTJ:stä.
     * Esimerkiksi sosiaalilautakunnan huostaanotto poistetaan VTJ:stä sosiaalilautakunnan päätöksellä
     * tai kun henkilö täyttää 18 vuotta.
     */
    POISTETTU(4L),

    /**
     * Virheellisen kirjauksen korjauksen yhteydessä annetaan aina myös korjattava tietoryhmä kokonaisuudessaan
     * (vanha virheellisen tiedon sisältävä tietoryhmä).
     */
    KORJATTAVAA(5L),

    /**
     * Korjattu tietoryhmä seuraa korjattavaa ja käsittää uuden, korjatun tietosisällön.
     */
    KORJATTU(6L);

    private final Long number;

    private final static Map<Long, Muutostapa> map =
            Arrays.stream(Muutostapa.values()).collect(Collectors.toMap(type -> type.number, type -> type));

    Muutostapa(final Long number) {
        this.number = number;
    }

    public Long getNumber() {
        return number;
    }

    public static Muutostapa get(Long number) {
        return map.get(number);
    }

}
