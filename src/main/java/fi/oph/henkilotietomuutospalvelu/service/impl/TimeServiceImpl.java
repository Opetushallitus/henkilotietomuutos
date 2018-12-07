package fi.oph.henkilotietomuutospalvelu.service.impl;

import fi.oph.henkilotietomuutospalvelu.service.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TimeServiceImpl implements TimeService {

    @Override
    public LocalDate getLocalDate() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

}
