package fi.oph.henkilotietomuutospalvelu.aspects;

import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class AuditlogAspect {
    private final AuditlogAspectHelper auditlogAspectHelper;

    @Around(value = "execution(public * fi.oph.henkilotietomuutospalvelu.service.HetuService.updateHetusToDb(*))" +
            "&& args(hetuDto)", argNames = "proceedingJoinPoint, hetuDto")
    private Object logCreateHenkilo(ProceedingJoinPoint proceedingJoinPoint, HetuDto hetuDto) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        this.auditlogAspectHelper.logAddHetuToVtjUpdateList(hetuDto);
        return result;
    }

}
