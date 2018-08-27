package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Tietoryhma;
import fi.oph.henkilotietomuutospalvelu.dto.type.MuutosType;
import fi.oph.henkilotietomuutospalvelu.service.MuutostietoParseService;
import fi.oph.henkilotietomuutospalvelu.service.parse.TietoryhmaParserUtil;
import fi.oph.henkilotietomuutospalvelu.service.parse.VRKParseUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MuutostietoParseServiceImpl implements MuutostietoParseService {

    @Override
    public MuutostietoDto deserializeMuutostietoLine(String line) {
        String[] parts = line.split("\\|");
        MuutostietoDto muutostietoDto = parseTunnisteosa(parts[0]);
        muutostietoDto.setTietoryhmat(deserializeTietoryhmat(parts));
        return muutostietoDto;
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

    private static List<Tietoryhma> deserializeTietoryhmat(String[] tietoryhmat) {
        List<Tietoryhma> ryhmat = new ArrayList<>();
        for (int i = 1; i <= tietoryhmat.length -1; i++) {
            Tietoryhma ryhma;
            if (i < tietoryhmat.length - 1) {
                ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhmat[i], tietoryhmat[i+1]);
            } else {
                ryhma = TietoryhmaParserUtil.deserializeTietoryhma(tietoryhmat[i]);
            }

            if (ryhma != null) {
                ryhmat.add(ryhma);
            }
        }
        return ryhmat;
    }
}
