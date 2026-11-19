package lt.gov.vpb.pdb.repository.jpa.patent.util;

import lt.gov.vpb.pdb.model.util.Matching;
import lt.gov.vpb.pdb.model.util.StringMatching;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.format.DateTimeFormatter.ofPattern;
import static lt.gov.vpb.pdb.model.util.StringMatching.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Criteria builder helper for building data and count queries.
 */
public class CriteriaBuilderHelper<D> {

    CriteriaBuilder criteriaBuilder;

    CriteriaQuery<D> criteriaQuery;
    CriteriaQuery<Long> countQuery;

    Class<D> rootClass;

    Root<D> itemRoot;
    Root<D> countItemRoot;

    Predicate predicate;
    Predicate countPredicate;

    //joins for multiple fields of the same entity
    Map<String, Join> joinsMap = new HashMap<>();
    Map<String, Join> countJoinsMap = new HashMap<>();

    public CriteriaBuilderHelper(EntityManager entityManager, Class<D> rootClass) {
        criteriaBuilder = entityManager.getCriteriaBuilder();

        criteriaQuery = criteriaBuilder.createQuery(rootClass);
        countQuery = criteriaBuilder.createQuery(Long.class);

        itemRoot = criteriaQuery.from(rootClass);
        countItemRoot = countQuery.from(rootClass);
    }

    public <T, U> void appendPredicate(Attribute<T, U> field, Matching matching, Object valueObj) {
        if (valueObj == null || isEmpty(valueOf(valueObj)) || isBlank(valueOf(valueObj)))
            return;

        Predicate predicateString;
        Predicate countPredicateString;

        if(field.getJavaType() == String.class) {
            String value = (String)valueObj;
            if (isEmpty(value) || isBlank(value))
                return;
            StringMatching sm = (StringMatching)matching;
            String likeValue = createLikeValue(sm, value.toLowerCase());
            predicateString = criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get(field.getName())), likeValue);
            countPredicateString = criteriaBuilder.like(criteriaBuilder.lower(countItemRoot.get(field.getName())), likeValue);
        } else if(field.getJavaType() == Number.class){
            Number value = (Number)valueObj;
            predicateString = criteriaBuilder.equal(itemRoot.get(field.getName()), value);
            countPredicateString = criteriaBuilder.equal(itemRoot.get(field.getName()), value);
        } else if(field.getJavaType() == LocalDate.class){
            LocalDate date = LocalDate.parse(valueOf(valueObj), ofPattern("yyyy/MM/dd"));
            Expression<LocalDate> dt = criteriaBuilder.literal(date);
            predicateString = criteriaBuilder.equal(itemRoot.get(field.getName()), dt);
            countPredicateString = criteriaBuilder.equal(itemRoot.get(field.getName()), date);
        } else {
            throw new RuntimeException(format("Only String and Number support is currently implemented. Type %s for field %s is not supported.", field.getJavaType(), field.getName()));
        }

        if (predicate == null) {
            predicate = predicateString;
        } else {
            predicate = criteriaBuilder.and(predicate, predicateString);
        }

        if (countPredicate == null) {
            countPredicate = countPredicateString;
        } else {
            countPredicate = criteriaBuilder.and(countPredicate, countPredicateString);
        }
    }

    public <T, U> void appendInPredicate(Attribute<T, U> field, Object... values) {
        if (values == null || values.length == 0) return;

        CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(itemRoot.get(field.getName()));
        CriteriaBuilder.In<Object> inClauseCount = criteriaBuilder.in(countItemRoot.get(field.getName()));

        boolean hasValues = false;

        for (Object value : values) {
            if (value != null) {
                Long longValue = (value instanceof Long) ? (Long) value : Long.valueOf(value.toString());
                inClause.value(longValue);
                inClauseCount.value(longValue);
                hasValues = true;
            }
        }

        if (!hasValues) return;

        predicate = (predicate == null) ? inClause : criteriaBuilder.and(predicate, inClause);
        countPredicate = (countPredicate == null) ? inClauseCount : criteriaBuilder.and(countPredicate, inClauseCount);
    }

    public <T, U> void appendJoinInPredicate(String joinField, Attribute<T, U> field, Object... values) {
        if (values == null || values.length == 0) return;

        Join<D, T> join;
        Join<D, T> countJoin;

        if (joinsMap.containsKey(joinField)) {
            join = joinsMap.get(joinField);
            countJoin = countJoinsMap.get(joinField);
        } else {
            join = itemRoot.join(joinField, JoinType.INNER);
            countJoin = countItemRoot.join(joinField, JoinType.INNER);
            joinsMap.put(joinField, join);
            countJoinsMap.put(joinField, countJoin);
        }

        CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(join.get(field.getName()));
        CriteriaBuilder.In<Object> inClauseCount = criteriaBuilder.in(countJoin.get(field.getName()));

        boolean hasValues = false;

        for (Object value : values) {
            if (value != null) {
                Long longValue = (value instanceof Long) ? (Long) value : Long.valueOf(value.toString());
                inClause.value(longValue);
                inClauseCount.value(longValue);
                hasValues = true;
            }
        }

        if (!hasValues) return;

        predicate = (predicate == null) ? inClause : criteriaBuilder.and(predicate, inClause);
        countPredicate = (countPredicate == null) ? inClauseCount : criteriaBuilder.and(countPredicate, inClauseCount);
    }


    public <T, U> void appendOrPredicate(Attribute<T, U> field, Object... values) {
        if (values == null || values.length == 0) return;

        List<Predicate> orPredicates = Arrays.stream(values)
                .filter(Objects::nonNull)
                .map(value -> criteriaBuilder.equal(itemRoot.get(field.getName()), value))
                .collect(Collectors.toList());

        List<Predicate> orPredicatesCount = Arrays.stream(values)
                .filter(Objects::nonNull)
                .map(value -> criteriaBuilder.equal(countItemRoot.get(field.getName()), value))
                .collect(Collectors.toList());

        if (!orPredicates.isEmpty()) {
            Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            Predicate orPredicateCount = criteriaBuilder.or(orPredicatesCount.toArray(new Predicate[0]));

            predicate = (predicate == null) ? orPredicate : criteriaBuilder.and(predicate, orPredicate);
            countPredicate = (countPredicate == null) ? orPredicateCount : criteriaBuilder.and(countPredicate, orPredicateCount);
        }
    }

    public <T, U> void appendStringTokensPredicate(Attribute<T, U> field, Matching sm, String value) {
        if (isEmpty(value) || isBlank(value))
            return;

        if( sm == CONTAINS){
            //String does not need to be tokenized. search as a whole string value.
            appendPredicate(field, CONTAINS, value);
            return;
        }

        Predicate orPredicate = criteriaBuilder.or();
        Predicate orPredicateCount = criteriaBuilder.or();

        StringTokenizer tokenizer = new StringTokenizer(value, " ");
        while(tokenizer.hasMoreElements()){
            String token = tokenizer.nextToken();
            if(sm == CONTAINS_ANY){
                String likeValue = createLikeValue(CONTAINS, token.toLowerCase());
                orPredicate = criteriaBuilder.or(orPredicate, criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get(field.getName())), likeValue));
                orPredicateCount = criteriaBuilder.or(orPredicateCount, criteriaBuilder.like(criteriaBuilder.lower(countItemRoot.get(field.getName())), likeValue));
            } else if (sm == CONTAINS_ALL) {
                appendPredicate(field, CONTAINS, token);
            }
        }

        if(sm == CONTAINS_ANY) {
            if (predicate == null) {
                predicate = orPredicate;
            } else {
                predicate = criteriaBuilder.and(predicate, orPredicate);
            }

            if (countPredicate == null) {
                countPredicate = orPredicateCount;
            } else {
                countPredicate = criteriaBuilder.and(countPredicate, orPredicateCount);
            }
        }


    }

    public <T, U> void appendJoinPredicate(String joinField, Attribute<T, U> field, StringMatching sm, String value) {
        appendJoinPredicate(joinField, field, sm, value, null);
    }

    public <T, U> void appendJoinPredicate(String joinField, Attribute<T, U> field, StringMatching sm, String value, String referencedTo) {

        if (isEmpty(value) || isBlank(value))
            return;

        Join<D, T> join;
        Join<D, T> countJoin;
        if(joinsMap.containsKey(joinField)){
            join = joinsMap.get(joinField);
            countJoin = countJoinsMap.get(joinField);
        } else if (referencedTo != null && joinsMap.containsKey(referencedTo)) {
            join = joinsMap.get(referencedTo).join(joinField);
            countJoin = countJoinsMap.get(referencedTo).join(joinField);
        } else {
            join = itemRoot.join(joinField, JoinType.INNER);
            countJoin = countItemRoot.join(joinField, JoinType.INNER);
            joinsMap.put(joinField, join);
            countJoinsMap.put(joinField, countJoin);
        }

        Predicate predicateString;
        Predicate countPredicateString;

        if(field.getJavaType() == String.class){
            String likeValue = createLikeValue(sm, value.toLowerCase());
            predicateString = criteriaBuilder.like(criteriaBuilder.lower(join.get(field.getName())), likeValue);
            countPredicateString = criteriaBuilder.like(criteriaBuilder.lower(countJoin.get(field.getName())), likeValue);
        } else if(field.getJavaType() == LocalDate.class){
                LocalDate date = LocalDate.parse(value, ofPattern("yyyy/MM/dd"));
                Expression<LocalDate> dt = criteriaBuilder.literal(date);
                predicateString = criteriaBuilder.equal(join.get(field.getName()), dt);
                countPredicateString = criteriaBuilder.equal(countJoin.get(field.getName()), date);
        } else {
            throw new RuntimeException(field.getJavaType() + " support not implemented in CriteriaBuilderHelper");
        }

        if (predicate == null) {
            predicate = predicateString;
        } else {
            predicate = criteriaBuilder.and(predicate, predicateString);
        }
        if (countPredicate == null) {
            countPredicate = countPredicateString;
        } else {
            countPredicate = criteriaBuilder.and(countPredicate, countPredicateString);
        }
    }

    public <T, U> void appendJoin(String joinField) {
        appendJoin(joinField, null);
    }

    public <T, U> void appendJoin(String joinField, String referencedTo) {
        Join<D, T> join;
        Join<D, T> countJoin;

        if (referencedTo != null && joinsMap.containsKey(referencedTo)) {
            join = joinsMap.get(referencedTo).join(joinField);
            countJoin = countJoinsMap.get(referencedTo).join(joinField);
        } else {
            join = itemRoot.join(joinField, JoinType.INNER);
            countJoin = countItemRoot.join(joinField, JoinType.INNER);
        }
        joinsMap.put(joinField, join);
        countJoinsMap.put(joinField, countJoin);
    }

    public void setOrder(String order, String field){
        criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get(field)));
    }

    public CriteriaQuery<D> getQuery() {
        CriteriaQuery query;
        if (predicate != null) {
            query = criteriaQuery.where(predicate);
        } else {
            query = criteriaQuery;
        }
        return query;
    }

    public CriteriaQuery<Long> getCountQuery() {
        countQuery = countQuery.select(criteriaBuilder.count(countItemRoot));
        countQuery = countQuery.select(criteriaBuilder.countDistinct(countItemRoot));
        if (countPredicate != null) {
            return countQuery.where(countPredicate);
        } else {
            return countQuery;
        }
    }


    private String createLikeValue(StringMatching sm, String value){
        switch(sm){
            case CONTAINS: return "%" + value + "%";
            case STARTS_WITH: return value + "%";
            case ENDS_WITH: return "%" + value;
            case EQUALS: return value;
            default: throw new RuntimeException(sm.getValue() + " support is not implemented for DB 'like' condition");
        }
    }

}
