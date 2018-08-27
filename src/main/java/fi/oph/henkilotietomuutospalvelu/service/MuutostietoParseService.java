package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;

public interface MuutostietoParseService {
    MuutostietoDto deserializeMuutostietoLine(String line);
}
