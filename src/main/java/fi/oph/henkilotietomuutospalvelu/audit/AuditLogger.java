package fi.oph.henkilotietomuutospalvelu.audit;

import fi.vm.sade.auditlog.*;
import fi.oph.henkilotietomuutospalvelu.config.ConfigEnums;
import lombok.extern.slf4j.Slf4j;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static fi.vm.sade.javautils.http.HttpServletRequestUtils.getRemoteAddress;

@Slf4j
public abstract class AuditLogger extends Audit {
    private static final String USER_AGENT = "User-Agent";

    AuditLogger(Logger logger, ApplicationType applicationType) {
        super(logger, ConfigEnums.SERVICENAME.value(), applicationType);
    }

    public void log(Operation operation, Target target, Changes changes) {
        this.log(getUser(), operation, target, changes);
    }

    private User getUser() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        try {
            InetAddress address = InetAddress.getByName(getRemoteAddress(sra.getRequest()));
            HttpServletRequest req = sra.getRequest();
            String session = req.getSession().getId();
            String userAgent = req.getHeader(USER_AGENT);

            return new User(getCurrentPersonOid(), address, session, userAgent);
        } catch (UnknownHostException e) {
            log.error("Error creating InetAddress: ", e);
            throw new RuntimeException(e);
        }

    }

    private Oid getCurrentPersonOid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            try {
                return new Oid(authentication.getName());
            } catch (GSSException e) {
                log.error("Error creating Oid-object out of {}", authentication.getName());
            }
        }
        return null;
    }
}
