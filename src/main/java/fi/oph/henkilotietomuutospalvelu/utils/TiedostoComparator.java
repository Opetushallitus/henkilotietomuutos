package fi.oph.henkilotietomuutospalvelu.utils;

import fi.oph.henkilotietomuutospalvelu.model.Tiedosto;
import fi.oph.henkilotietomuutospalvelu.service.FileService;

import java.util.Comparator;

public class TiedostoComparator implements Comparator<Tiedosto> {

    private final TiedostoNimiComparator nimiComparator;

    public TiedostoComparator(FileService fileService) {
        this.nimiComparator = new TiedostoNimiComparator(fileService);
    }

    @Override
    public int compare(Tiedosto o1, Tiedosto o2) {
        return nimiComparator.compare(o1.getFileName(), o2.getFileName());
    }

}
