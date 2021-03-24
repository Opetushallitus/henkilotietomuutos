package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.service.exception.MuutostietoLineParseException;
import fi.oph.henkilotietomuutospalvelu.service.impl.MuutostietoLine;

public interface MuutostietoParseService {
    /**
     * Deserialize line into a <code>MuutostietoDto</code>.
     * @param line data line
     * @return deserialized DTO
     * @throws MuutostietoLineParseException if parsing the data fails
     */
    MuutostietoDto deserializeMuutostietoLine(MuutostietoLine line) throws MuutostietoLineParseException;

    /**
     * Serialize a <code>MuutostietoDto</code> to a String.
     * @param dto dto to serialize
     * @return dto serialized
     */
    String serializeMuutostietoDto(MuutostietoDto dto);
}
