package fi.oph.henkilotietomuutospalvelu.repository.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
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
        QHenkilotunnuskorjaus qHenkilotunnuskorjaus1 = new QHenkilotunnuskorjaus("henkilotunnuskorjaus1");
        QHenkilotunnuskorjaus qHenkilotunnuskorjaus2 = new QHenkilotunnuskorjaus("henkilotunnuskorjaus2");

        List<String> hetut = new JPAQuery<>(entityManager)
                .from(qHenkilotunnuskorjaus1)
                .where(qHenkilotunnuskorjaus1.henkiloMuutostietoRivi.id.in(JPAExpressions
                        .selectFrom(qHenkilotunnuskorjaus2)
                        .select(qHenkilotunnuskorjaus2.henkiloMuutostietoRivi.id)
                        .where(qHenkilotunnuskorjaus2.hetu.eq(hetu))))
                .select(qHenkilotunnuskorjaus1.hetu)
                .distinct()
                .fetch();
        return new LinkedHashSet<>(hetut);
    }

}
