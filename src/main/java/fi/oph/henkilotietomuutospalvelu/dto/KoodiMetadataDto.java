package fi.oph.henkilotietomuutospalvelu.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KoodiMetadataDto implements Serializable {

    private String kieli;
    private String nimi;

}
