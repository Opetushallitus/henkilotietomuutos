package fi.oph.henkilotietomuutospalvelu.service.exception;

public class TietoryhmaParseException extends RuntimeException {

    public TietoryhmaParseException() {
        super();
    }

    public TietoryhmaParseException(String message) {
        super(message);
    }

    public TietoryhmaParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
