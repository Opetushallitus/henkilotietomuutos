package fi.oph.henkilotietomuutospalvelu.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import fi.oph.henkilotietomuutospalvelu.model.QVtjDataEvent;
import fi.oph.henkilotietomuutospalvelu.model.VtjDataEvent;
import fi.oph.henkilotietomuutospalvelu.repository.VtjDataRepositoryCustom;
import org.springframework.data.jpa.repository.JpaContext;

import javax.persistence.EntityManager;
import java.util.Optional;

public class VtjDataRepositoryImpl implements VtjDataRepositoryCustom {

    private final EntityManager entityManager;

    public VtjDataRepositoryImpl(JpaContext jpaContext) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(VtjDataEvent.class);
    }

    @Override
    public Optional<VtjDataEvent> findLatestByHetu(String hetu) {
        QVtjDataEvent qVtjDataEvent = QVtjDataEvent.vtjDataEvent;
        VtjDataEvent vtjDataEvent = new JPAQuery<>(entityManager)
                .from(qVtjDataEvent)
                .select(qVtjDataEvent)
                .where(qVtjDataEvent.hetu.eq(hetu))
                .orderBy(qVtjDataEvent.vtjdataTimestamp.desc().nullsLast(), qVtjDataEvent.id.desc())
                .fetchFirst();
        return Optional.ofNullable(vtjDataEvent);
    }

}
