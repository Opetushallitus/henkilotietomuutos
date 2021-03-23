package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Muutostapa;
import fi.oph.henkilotietomuutospalvelu.dto.type.NameType;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkiloNameChange;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;

import java.util.Arrays;
import java.util.List;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseCharacter;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HenkiloNameChangeParser implements TietoryhmaParser {

    /** Henkilön nimen laji. */
    private enum NimiLaji {
        NYKYINEN_SUKUNIMI("01"),
        NYKYISET_ETUNIMET("02"),
        NYKYINEN_VALINIMI("03"),
        NYKYINEN_KUTSUMANIMI("04"),
        ENTINEN_SUKUNIMI("05"),
        ENTISET_ETUNIMET("06"),
        VIIMEISIN_SUKUNIMI_NAIMATTOMANA("07"),
        ENTINEN_VALINIMI("08"), // ei oteta toistaiseksi käyttöön
        ENTINEN_KUTSUMANIMI("09"), // ei oteta toistaiseksi käyttöön
        KORJATTU_SUKUNIMI("10"),
        KORJATUT_ETUNIMET("11"),
        KORJATTU_VALINIMI("12"),
        KORJATTU_KUTSUMANIMI("13"), // ei oteta toistaiseksi käyttöön
        PATRONYYMI("14"), // ei oteta toistaiseksi käyttöön
        JURIDISEN_HENKILON_NIMI("15");

        private final String laji;

        NimiLaji(String laji) {
            this.laji = laji;
        }

        static NimiLaji fromLaji(String laji) {
            return Arrays.stream(NimiLaji.values()).filter(
                    nimiLaji -> nimiLaji.laji.equals(laji)
            ).findFirst().orElseThrow(
                    () -> new IllegalArgumentException("Tuntematon nimen laji: " + laji)
            );
        }
    }
    private static final List<NimiLaji> SUKUNIMI_LAJIT = Arrays.asList(
            NimiLaji.NYKYINEN_SUKUNIMI, NimiLaji.ENTINEN_SUKUNIMI, NimiLaji.VIIMEISIN_SUKUNIMI_NAIMATTOMANA,
            NimiLaji.KORJATTU_SUKUNIMI);
    private static final List<NimiLaji> ETUNIMI_LAJIT = Arrays.asList(
            NimiLaji.NYKYISET_ETUNIMET, NimiLaji.ENTISET_ETUNIMET, NimiLaji.KORJATUT_ETUNIMET);
    private static final List<NimiLaji> VALINIMI_LAJIT = Arrays.asList(
            NimiLaji.NYKYINEN_VALINIMI, NimiLaji.ENTINEN_VALINIMI, NimiLaji.KORJATTU_VALINIMI);
    private static final List<NimiLaji> KUTSUMANIMI_LAJIT = Arrays.asList(
            NimiLaji.NYKYINEN_KUTSUMANIMI, NimiLaji.ENTINEN_KUTSUMANIMI, NimiLaji.KORJATTU_KUTSUMANIMI);

    @Override
    public HenkiloNameChange parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        String laji = parseString(tietoryhma, 104, 2);
        NimiLaji nimiLaji = NimiLaji.fromLaji(laji);
        NameType type = NameType.TUNTEMATON;
        if (SUKUNIMI_LAJIT.contains(nimiLaji)) {
            type = NameType.SUKUNIMI;
        } else if (ETUNIMI_LAJIT.contains(nimiLaji)) {
            type = NameType.ETUNIMI;
        } else if (VALINIMI_LAJIT.contains(nimiLaji)) {
            type = NameType.VALINIMI;
        } else if (KUTSUMANIMI_LAJIT.contains(nimiLaji)) {
            type = NameType.KUTSUMANIMI;
        }

        return HenkiloNameChange.builder()
                .ryhmatunnus(Ryhmatunnus.HENKILO_NIMENMUUTOS)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .name(parseString(tietoryhma,4, 100))
                .nameType(type)
                .startDate(parseDate(tietoryhma, 106))
                .endDate(parseDate(tietoryhma, 114))
                .hasNonStandardCharacters(parseCharacter(tietoryhma, 122).equals("1"))
                .build();
    }

    @Override
    public String serialize(Tietoryhma tietoryhma) {
        HenkiloNameChange change = (HenkiloNameChange) tietoryhma;
        return Ryhmatunnus.HENKILO_NIMENMUUTOS.getCode()
                + change.getMuutostapa().getNumber()
                + serializeString(change.getName(), 100)
                + serializeString(deduceNimiLaji(change.getNameType(), change.getMuutostapa()).laji, 2)
                + serializeDate(change.getStartDate())
                + serializeDate(change.getEndDate())
                + (change.getHasNonStandardCharacters() ? "1" : " ");
    }

    // Huom! Nimilajin päättely ei toimi järin luotettavasti, koska nimilajia ei tallenneta.
    // Pohjautuu vankasti oletuksille, tarkoitettu ainoastaan testikäyttöön.

    private static NimiLaji deduceNimiLaji(NameType nimiTyyppi, Muutostapa muutostapa) {
        switch (nimiTyyppi) {
            case SUKUNIMI: return deduceSukunimiNimiLaji(muutostapa);
            case ETUNIMI: return deduceEtunimetNimiLaji(muutostapa);
            case VALINIMI: return deduceValinimiNimiLaji(muutostapa);
            case KUTSUMANIMI: return deduceKutsumanimiNimiLaji(muutostapa);
            default: return NimiLaji.JURIDISEN_HENKILON_NIMI; // -> NameType.TUNTEMATON
        }
    }

    private static NimiLaji deduceSukunimiNimiLaji(Muutostapa muutostapa) {
        switch(muutostapa) {
            case KORJATTU: return NimiLaji.KORJATTU_SUKUNIMI;
            case LISATTY: return NimiLaji.NYKYINEN_SUKUNIMI;
            default: return NimiLaji.ENTINEN_SUKUNIMI;
        }
    }

    private static NimiLaji deduceEtunimetNimiLaji(Muutostapa muutostapa) {
        switch(muutostapa) {
            case KORJATTU: return NimiLaji.KORJATUT_ETUNIMET;
            case LISATTY: return NimiLaji.NYKYISET_ETUNIMET;
            default: return NimiLaji.ENTISET_ETUNIMET;
        }
    }

    private static NimiLaji deduceValinimiNimiLaji(Muutostapa muutostapa) {
        switch (muutostapa) {
            case KORJATTU: return NimiLaji.KORJATTU_VALINIMI;
            case LISATTY: return NimiLaji.NYKYINEN_VALINIMI;
            default: return NimiLaji.ENTINEN_VALINIMI;
        }
    }

    private static NimiLaji deduceKutsumanimiNimiLaji(Muutostapa muutostapa) {
        switch (muutostapa) {
            case KORJATTU: return NimiLaji.KORJATTU_KUTSUMANIMI;
            case LISATTY: return NimiLaji.NYKYINEN_KUTSUMANIMI;
            default: return NimiLaji.ENTINEN_KUTSUMANIMI;
        }
    }
}
