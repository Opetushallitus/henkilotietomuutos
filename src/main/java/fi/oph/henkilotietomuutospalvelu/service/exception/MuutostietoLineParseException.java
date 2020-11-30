package fi.oph.henkilotietomuutospalvelu.service.exception;

import fi.oph.henkilotietomuutospalvelu.service.impl.MuutostietoLine;

public class MuutostietoLineParseException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Parsing Muutostieto failed on row: %d";

    public MuutostietoLineParseException(MuutostietoLine line, RuntimeException cause) {
        super(String.format(MESSAGE_FORMAT, line.lineNumber), cause);
    }

}
