package fi.oph.henkilotietomuutospalvelu.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Setter
@MappedSuperclass
public class IdentifiableAndVersionedEntity implements Persistable<Long> {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Override
    public boolean isNew() {
        return null == this.getId();
    }
}
