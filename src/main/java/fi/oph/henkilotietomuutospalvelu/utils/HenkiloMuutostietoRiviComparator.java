package fi.oph.henkilotietomuutospalvelu.utils;

import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;

import java.util.Comparator;

public class HenkiloMuutostietoRiviComparator implements Comparator<HenkiloMuutostietoRivi> {

    @Override
    public int compare(HenkiloMuutostietoRivi o1, HenkiloMuutostietoRivi o2) {
        return Comparator.comparing(HenkiloMuutostietoRivi::getRivi, Integer::compare).compare(o1, o2);
    }

}
