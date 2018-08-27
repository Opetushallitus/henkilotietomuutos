package fi.oph.henkilotietomuutospalvelu.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED, reason = "Sync using VTJ Muutostietopalvelu not yet implemented")
public class NotImplementedException extends RuntimeException {}
