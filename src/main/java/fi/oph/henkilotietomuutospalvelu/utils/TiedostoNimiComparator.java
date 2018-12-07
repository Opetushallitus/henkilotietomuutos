package fi.oph.henkilotietomuutospalvelu.utils;

import fi.oph.henkilotietomuutospalvelu.service.FileService;

import java.util.Comparator;
import java.util.Objects;

public class TiedostoNimiComparator implements Comparator<String> {

    private final Comparator<String> comparator;

    public TiedostoNimiComparator(FileService fileService) {
        this.comparator = fileService.byFileExtension().thenComparing(fileService.bySequentalNumbering());
    }

    @Override
    public int compare(String o1, String o2) {
        return Objects.compare(o1, o2, comparator);
    }

}
