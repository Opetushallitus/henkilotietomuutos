package fi.oph.henkilotietomuutospalvelu.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Problem while accessing ONR service")
public class OnrServiceClientException extends Exception {
    public OnrServiceClientException(Exception e) { super(e); }
    public OnrServiceClientException(String m) { super(m); }
}
