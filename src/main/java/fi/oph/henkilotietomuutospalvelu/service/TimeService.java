package fi.oph.henkilotietomuutospalvelu.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TimeService {

    LocalDate getLocalDate();

    LocalDateTime getLocalDateTime();

}
