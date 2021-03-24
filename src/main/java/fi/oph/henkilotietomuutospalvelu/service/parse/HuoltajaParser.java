package fi.oph.henkilotietomuutospalvelu.service.parse;

import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.HenkilotunnuksetonHenkilo;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Oikeus;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseMuutosTapa;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseRyhmatunnus;
import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseString;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeDate;
import static fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil.serializeString;

public class HuoltajaParser implements TietoryhmaParser<Huoltaja> {

    private static final HenkilotunnuksetonHenkiloParser HENKILO_PARSER = HenkilotunnuksetonHenkiloParser.INSTANCE;
    private static final OikeusParser OIKEUS_PARSER = OikeusParser.INSTANCE;

    public Huoltaja parse(String tietoryhma, String... tarkentavatTietoryhmat) {
        if (tietoryhma.length() == 43) {
            // formaatti ennen 2019-12-01
            return Huoltaja.builder()
                    .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                    .muutostapa(parseMuutosTapa(tietoryhma))
                    .hetu(parseString(tietoryhma,4, 11))
                    //.laji(parseString(tietoryhma, 15, 2)) tiedon sisältö muuttunut uudessa versiossa
                    //.huollonjako(Huollonjako.getEnum(parseCharacter(tietoryhma, 17))) tietoa ei enää uudessa formaatissa
                    //.voimassa(parseCharacter(tietoryhma, 18).equals("1")) tietoa ei enää uudessa formaatissa
                    .startDate(parseDate(tietoryhma, 19))
                    .endDate(parseDate(tietoryhma, 27))
                    //.resolutionDate(parseDate(tietoryhma, 35)) // tietoa ei enää uudessa formaatissa
                    .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                    .build();
        }
        return Huoltaja.builder()
                .ryhmatunnus(Ryhmatunnus.HUOLTAJA)
                .muutostapa(parseMuutosTapa(tietoryhma))
                .hetu(parseString(tietoryhma, 4, 11))
                .laji(parseString(tietoryhma, 15, 1))
                .rooli(parseString(tietoryhma, 16, 1))
                .startDate(parseDate(tietoryhma, 17))
                .endDate(parseDate(tietoryhma, 25))
                .asuminen(parseString(tietoryhma, 33, 1))
                .asuminenAlkupvm(parseDate(tietoryhma, 34))
                .asuminenLoppupvm(parseDate(tietoryhma, 42))
                .henkilotunnuksetonHenkilo(parseHenkilotunnuksetonHenkilo(tarkentavatTietoryhmat))
                .oikeudet(parseOikeudet(tarkentavatTietoryhmat))
                .build();
    }

    private HenkilotunnuksetonHenkilo parseHenkilotunnuksetonHenkilo(String... tarkentavatTietoryhmat) {
        Optional<String> henkiloTietoryhma = Arrays.stream(tarkentavatTietoryhmat)
                .filter(tietoryhma ->
                        Ryhmatunnus.HENKILOTUNNUKSETON_HENKILO.getCode().equals(parseRyhmatunnus(tietoryhma)))
                .findFirst();
        if (henkiloTietoryhma.isPresent()) {
            String henkiloTarkentavaTietoryhma = Arrays.stream(tarkentavatTietoryhmat)
                    .filter(tietoryhma ->
                            Ryhmatunnus.LISATIETO.getCode().equals(parseRyhmatunnus(tietoryhma)))
                    .findFirst().orElse(null);
            return HENKILO_PARSER.parse(henkiloTietoryhma.get(),
                    henkiloTarkentavaTietoryhma != null ? new String[] { henkiloTarkentavaTietoryhma } : new String[0]);
        }
        return null;
    }

    // formaatti ennen 2019-12-01 ei tuettu!
    public String serialize(Huoltaja huoltaja) {
        String serialized = Ryhmatunnus.HUOLTAJA.getCode()
                + huoltaja.getMuutostapa().getNumber()
                + serializeString(huoltaja.getHetu(), 11)
                + serializeString(huoltaja.getLaji(), 1)
                + serializeString(huoltaja.getRooli(), 1)
                + serializeDate(huoltaja.getStartDate())
                + serializeDate(huoltaja.getEndDate())
                + serializeString(huoltaja.getAsuminen(), 1)
                + serializeDate(huoltaja.getAsuminenAlkupvm())
                + serializeDate(huoltaja.getAsuminenLoppupvm());
        if (huoltaja.getHenkilotunnuksetonHenkilo() != null) {
            serialized = String.join("|", serialized,
                    HENKILO_PARSER.serialize(huoltaja.getHenkilotunnuksetonHenkilo()));
        }
        if (huoltaja.getOikeudet() != null && !huoltaja.getOikeudet().isEmpty()) {
            serialized = String.join("|", serialized, huoltaja.getOikeudet().stream().map(
                    OIKEUS_PARSER::serialize
            ).collect(Collectors.joining("|")));
        }
        return serialized;
    }

    private static Set<Oikeus> parseOikeudet(String... tietoryhmat) {
        return Arrays.stream(tietoryhmat)
                .filter(tietoryhma -> Ryhmatunnus.OIKEUS.getCode().equals(parseRyhmatunnus(tietoryhma)))
                .map(OIKEUS_PARSER::parse)
                .collect(Collectors.toSet());
    }

}
