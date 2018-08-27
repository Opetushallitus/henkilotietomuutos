package fi.oph.henkilotietomuutospalvelu.service.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Exception e) {
        super(e);
    }

}
