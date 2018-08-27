package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.KoodiDto;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;

import java.util.List;
import java.util.Map;

public interface MuutostietoHandleService {
    void importUnprocessedMuutostiedotToDb(List<MuutostietoDto> muutostietoDtoList, String fileName);

    void handleMuutostieto(HenkiloMuutostietoRivi henkiloMuutostietoRivi, Map<String, KoodiDto> postitoimipaikat, Map<String, KoodiDto> maat);
}
