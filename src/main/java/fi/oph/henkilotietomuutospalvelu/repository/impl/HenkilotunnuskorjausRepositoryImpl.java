package fi.oph.henkilotietomuutospalvelu.repository.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import fi.oph.henkilotietomuutospalvelu.model.QHenkiloMuutostietoRivi;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.Henkilotunnuskorjaus;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.QHenkilotunnuskorjaus;
import fi.oph.henkilotietomuutospalvelu.repository.HenkilotunnuskorjausRepositoryCustom;
import org.springframework.data.jpa.repository.JpaContext;

import javax.persistence.EntityManager;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HenkilotunnuskorjausRepositoryImpl implements HenkilotunnuskorjausRepositoryCustom {

    private final EntityManager entityManager;

    public HenkilotunnuskorjausRepositoryImpl(JpaContext jpaContext) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(Henkilotunnuskorjaus.class);
    }

    public Set<String> findHetuByHenkilotunnuskorjausHetu(String hetu) {
        QHenkilotunnuskorjaus qHenkilotunnuskorjaus = QHenkilotunnuskorjaus.henkilotunnuskorjaus;
        QHenkilotunnuskorjaus qHenkilotunnuskorjausSub = new QHenkilotunnuskorjaus("qHenkilotunnuskorjausSub");
        QHenkiloMuutostietoRivi qHenkiloMuutostietoRiviSub = QHenkiloMuutostietoRivi.henkiloMuutostietoRivi;

        List<String> hetut = new JPAQuery<>(entityManager)
                .from(qHenkilotunnuskorjaus)
                .where(qHenkilotunnuskorjaus.henkiloMuutostietoRivi.in(JPAExpressions
                        .select(qHenkiloMuutostietoRiviSub)
                        .from(qHenkilotunnuskorjausSub)
                        .join(qHenkilotunnuskorjausSub.henkiloMuutostietoRivi, qHenkiloMuutostietoRiviSub)
                        .where(qHenkilotunnuskorjausSub.hetu.eq(hetu))))
                .select(qHenkilotunnuskorjaus.hetu)
                .distinct()
                .fetch();
        return new LinkedHashSet<>(hetut);
    }

}
