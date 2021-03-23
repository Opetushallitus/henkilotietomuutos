package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.MuutosType;
import fi.oph.henkilotietomuutospalvelu.dto.type.Ryhmatunnus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Huoltaja;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoParseService;
import fi.oph.henkilotietomuutospalvelu.service.exception.MuutostietoLineParseException;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParser;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil;
import fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil.parseRyhmatunnus;

@Service
public class MuutostietoParseServiceImpl implements MuutostietoParseService {

    @Override
    public MuutostietoDto deserializeMuutostietoLine(MuutostietoLine line) throws MuutostietoLineParseException {
        String[] parts = line.content.split("\\|");
        try {
            MuutostietoDto muutostietoDto = parseTunnisteosa(parts[0]);
            muutostietoDto.setTietoryhmat(deserializeTietoryhmat(parts));
            return muutostietoDto;
        } catch (RuntimeException e) {
            throw new MuutostietoLineParseException(line, e);
        }
    }

    @Override
    public String serializeMuutostietoDto(MuutostietoDto dto) {
        String serialized = serializeTunnisteosa(dto);
        for (Tietoryhma ryhma : dto.getTietoryhmat()) {
            serialized = String.join("|", serialized, serializeTietoryhma(ryhma));
        }
        return serialized + "|";
    }

    @SuppressWarnings("unchecked")
    private <T extends Tietoryhma> String serializeTietoryhma(T tietoryhma) {
        TietoryhmaParser<T> parser = (TietoryhmaParser<T>) tietoryhma.getRyhmatunnus().getParser();
        return parser.serialize(tietoryhma);
    }

    private static MuutostietoDto parseTunnisteosa(String tunnisteosa) {
        return MuutostietoDto.builder()
            .hetu(tunnisteosa.substring(0, 11))
            .tapahtuma(tunnisteosa.substring(11, 14))
            .rekisterointipaiva(VRKParseUtil.deserializeDate(tunnisteosa.substring(14, 22)))
            .muutosType(MuutosType.getEnum(tunnisteosa.substring(22, 23)))
            .role(tunnisteosa.substring(23, 24))
            .build();
    }

    private static String serializeTunnisteosa(MuutostietoDto dto) {
        return dto.getHetu()
                + dto.getTapahtuma()
                + VRKParseUtil.serializeDate(dto.getRekisterointipaiva())
                + dto.getMuutosType().getCode()
                + dto.getRole();
    }

    private static List<Tietoryhma> deserializeTietoryhmat(String[] tietoryhmat) {
        List<Tietoryhma> ryhmat = new ArrayList<>();
        for (int i = 1; i <= tietoryhmat.length - 1; i++) {
            List<String> tarkentavatTietoryhmat = etsiTarkentavatTietoryhmat(tietoryhmat, i);
            Tietoryhma ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhmat[i],
                    tarkentavatTietoryhmat.toArray(new String[0]));

            ryhmat.add(ryhma);
            if (ryhma instanceof Huoltaja && ((Huoltaja)ryhma).getHenkilotunnuksetonHenkilo() != null) {
                ryhmat.add(((Huoltaja)ryhma).getHenkilotunnuksetonHenkilo());
            }

            i += tarkentavatTietoryhmat.size(); // tarkentavat tietoryhmät ohitetaan koska ne on jo käsitelty
        }
        return ryhmat;
    }

    private static List<String> etsiTarkentavatTietoryhmat(String[] tietoryhmat, int tietoryhmaIndex) {
        List<String> tarkentavatTietoryhmat = new ArrayList<>();
        for (int index = tietoryhmaIndex + 1; index <= tietoryhmat.length - 1; index++) {
            String tarkentavaTietoryhma = tietoryhmat[index];
            if (isTarkentavaTietoryhma(tarkentavaTietoryhma)) {
                tarkentavatTietoryhmat.add(tarkentavaTietoryhma);
            } else break;
        }
        return tarkentavatTietoryhmat;
    }

    private static boolean isTarkentavaTietoryhma(String tietoryhmaStr) {
        String ryhmatunnusStr = parseRyhmatunnus(tietoryhmaStr);
        Ryhmatunnus ryhmatunnus = Ryhmatunnus.getEnum(ryhmatunnusStr);
        return ryhmatunnus.isTarkentava();
    }
}
