package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;

import java.util.List;

public interface MuutostietoHandleService {
    void importUnprocessedMuutostiedotToDb(List<MuutostietoDto> muutostietoDtoList, String fileName);

    void handleMuutostieto(HenkiloMuutostietoRivi henkiloMuutostietoRivi);
}
