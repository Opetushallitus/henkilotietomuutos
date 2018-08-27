package fi.oph.henkilotietomuutospalvelu.audit;

import fi.vm.sade.auditlog.ApplicationType;
import org.springframework.stereotype.Component;

@Component
public class MuutostietoAuditLogger extends AuditLogger {

    public MuutostietoAuditLogger() {
        super(new AuditHelper(), ApplicationType.BACKEND);
    }

}
