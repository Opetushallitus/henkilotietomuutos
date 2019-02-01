package fi.oph.henkilotietomuutospalvelu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tiedosto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tiedosto extends IdentifiableAndVersionedEntity {
    @Column(name = "tiedosto_nimi", unique = true)
    private String fileName;

    // Number of parts this file has
    @Column(name = "part_count", nullable = false)
    private Integer partCount;

    public boolean isPerustietoaineisto() {
        String extension = FilenameUtils.getExtension(fileName);
        return "PTT".equals(extension);
    }

}
