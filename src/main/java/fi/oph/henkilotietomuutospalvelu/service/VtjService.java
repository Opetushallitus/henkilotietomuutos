package fi.oph.henkilotietomuutospalvelu.service;

import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloForceUpdateDto;

/**
 * Palvelu joka tarjoaa suoraan haettavia VTJ:n tietoja
 */
public interface VtjService {
    /**
     * Rikastaa henkilön pelkästään hetun sisältävien huoltajien tiedot VTJ tiedoilla ja "yksilöi" tämän. Koska nämä
     * hetut tulevat VTJ:stä niin häntä ei tarvitse tunnistaa erikseen vaan käytännössä lisätään vain tietoja ja
     * merkataan huoltaja yksilöidyksi.
     * @param henkiloForceUpdateDto Päivitettävän henkilön tiedot
     */
    void rikastaHuoltajatVtjTiedoilla(HenkiloForceUpdateDto henkiloForceUpdateDto);
}
