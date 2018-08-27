package fi.oph.henkilotietomuutospalvelu.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.oph.henkilotietomuutospalvelu.model.HenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.QHenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.QTiedosto;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.QHenkilotunnuskorjaus;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class HenkiloMuutostietoRepositoryImpl implements HenkiloMuutostietoRepositoryCustom {
    private final EntityManager entityManager;

    public HenkiloMuutostietoRepositoryImpl(JpaContext jpaContext) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(HenkiloMuutostietoRivi.class);
    }

    public JPAQueryFactory jpa() {
        return new JPAQueryFactory(this.entityManager);
    }


    @Override
    public List<String> findDistinctUnprocessedTiedostoFileName() {
        QHenkiloMuutostietoRivi henkiloMuutostietoRivi = QHenkiloMuutostietoRivi.henkiloMuutostietoRivi;
        QTiedosto tiedosto = QTiedosto.tiedosto;

        return jpa()
                .from(henkiloMuutostietoRivi)
                .innerJoin(henkiloMuutostietoRivi.tiedosto, tiedosto)
                .where(henkiloMuutostietoRivi.processTimestamp.isNull())
                .select(tiedosto.fileName)
                .distinct()
                .fetch();
    }

    @Override
    public Optional<Integer> findLastRowByTiedostoNimi(String fileName) {
        QHenkiloMuutostietoRivi henkiloMuutostietoRivi = QHenkiloMuutostietoRivi.henkiloMuutostietoRivi;
        QTiedosto tiedosto = QTiedosto.tiedosto;

        return Optional.ofNullable(jpa().from(henkiloMuutostietoRivi)
                .innerJoin(henkiloMuutostietoRivi.tiedosto, tiedosto)
                .select(henkiloMuutostietoRivi.rivi.max())
                .where(tiedosto.fileName.eq(fileName))
                .fetchFirst());
    }

    @Override
    public List<HenkiloMuutostietoRivi> findHenkiloMuutostietoRiviByQueryHetu(String queryHetu) {
        QHenkiloMuutostietoRivi henkiloMuutostietoRivi = QHenkiloMuutostietoRivi.henkiloMuutostietoRivi;
        QHenkilotunnuskorjaus tietoryhma = QHenkilotunnuskorjaus.henkilotunnuskorjaus;

        return jpa().select(henkiloMuutostietoRivi)
                .from(tietoryhma)
                .innerJoin(tietoryhma.henkiloMuutostietoRivi, henkiloMuutostietoRivi)
                .where(henkiloMuutostietoRivi.queryHetu.eq(queryHetu))
                .where(tietoryhma.active.isTrue())
                .fetch();
    }
}
