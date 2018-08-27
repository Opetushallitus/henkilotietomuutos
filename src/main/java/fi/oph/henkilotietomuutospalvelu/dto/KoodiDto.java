package fi.oph.henkilotietomuutospalvelu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class KoodiDto {

    private String koodiArvo;
    private Collection<KoodiMetadataDto> metadata;

}
