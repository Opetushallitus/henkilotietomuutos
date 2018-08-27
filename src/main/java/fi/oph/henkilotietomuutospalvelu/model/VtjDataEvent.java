package fi.oph.henkilotietomuutospalvelu.model;

import fi.oph.henkilotietomuutospalvelu.model.type.VtjEventType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vtj_data_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VtjDataEvent extends IdentifiableAndVersionedEntity {
    @Column(name = "hetu")
    private String hetu;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private VtjEventType type;

    @Column(name = "vtjdata_timestamp")
    private LocalDateTime vtjdataTimestamp;
}
