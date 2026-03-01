package com.krishiYatra.krishiYatra.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;

import java.util.List;

import jakarta.persistence.TypedQuery;

public class DaoUtil {

    public static List<Tuple> executeCriteriaQuery(CriteriaQuery<Tuple> cq, Pageable pageable, EntityManager em) {
        TypedQuery<Tuple> query = em.createQuery(cq);
        if (pageable != null && pageable.isPaged()) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    public static void setPredicateToArray(List<Predicate> predicates, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (predicates != null && !predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }
}
