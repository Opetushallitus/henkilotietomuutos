package fi.oph.henkilotietomuutospalvelu.service.exception;

public class MuutostietoLineParseException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Parsing Muutostieto failed on row: %d";

    public MuutostietoLineParseException(int row, RuntimeException cause) {
        super(String.format(MESSAGE_FORMAT, row), cause);
    }

}
