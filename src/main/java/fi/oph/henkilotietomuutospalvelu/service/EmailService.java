package fi.oph.henkilotietomuutospalvelu.service;

public interface EmailService {
    void sendEmail(String topic, String message);
}
