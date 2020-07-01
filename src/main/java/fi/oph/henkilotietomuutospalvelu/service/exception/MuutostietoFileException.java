package fi.oph.henkilotietomuutospalvelu.service.exception;

public class MuutostietoFileException extends Exception {

    private static final String MESSAGE_FORMAT = "Failure handling Muutostieto file: %s";

    public MuutostietoFileException(String filePath, Throwable cause) {
        super(String.format(MESSAGE_FORMAT, filePath), cause);
    }

}
