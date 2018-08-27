package fi.oph.henkilotietomuutospalvelu.aspects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.oph.henkilotietomuutospalvelu.audit.MuutostietoAuditLogger;
import fi.oph.henkilotietomuutospalvelu.audit.MuutostietoMessageFields;
import fi.oph.henkilotietomuutospalvelu.audit.MuutostietoOperation;
import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class AuditlogAspectHelper {

    private final MuutostietoAuditLogger muutostietoLogger;
    private final ObjectMapper objectMapper;

    void logAddHetuToVtjUpdateList(HetuDto hetuDto) {
        Target target = new Target.Builder()
                .setField(MuutostietoMessageFields.LISATIETO, "Lisää ja poistaa VTJ päivitettäviä henkilöitä listalta")
                .build();
        Changes.Builder changeBuilder = new Changes.Builder();
        try {
            if (!CollectionUtils.isEmpty(hetuDto.getAddedHetus())) {
                changeBuilder.added("hetu", objectMapper.writeValueAsString(hetuDto.getAddedHetus()));
            }
            if (!CollectionUtils.isEmpty(hetuDto.getRemovedHetus())) {
                changeBuilder.removed("hetu", objectMapper.writeValueAsString(hetuDto.getRemovedHetus()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Changes changes = changeBuilder.build();
        this.muutostietoLogger.log(MuutostietoOperation.ADD_REMOVE_HETUS, target, changes);
    }

}
