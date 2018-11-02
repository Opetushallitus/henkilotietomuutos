package fi.oph.henkilotietomuutospalvelu.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KoodiDto implements Serializable {

    private String koodiArvo;
    private Collection<KoodiMetadataDto> metadata;

}
