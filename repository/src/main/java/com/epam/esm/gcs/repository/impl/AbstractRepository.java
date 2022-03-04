//package com.epam.esm.gcs.repository.impl;
//
//import com.epam.esm.gcs.exception.WiredEntityDeletionException;
//import com.epam.esm.gcs.model.NamedModel_;
//import com.epam.esm.gcs.repository.CrdRepository;
//import com.epam.esm.gcs.repository.Flushable;
//import com.epam.esm.gcs.util.Limiter;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.NoResultException;
//import javax.persistence.PersistenceContext;
//import javax.persistence.PersistenceException;
//import java.lang.reflect.ParameterizedType;
//import java.util.List;
//import java.util.Optional;
//
//public abstract class AbstractRepository<T>
//        implements CrdRepository<T>, Flushable {
//
//    private static final String FIND_ALL_QUERY = "SELECT m FROM %s m";
//    private static final String DELETE_BY_ID_QUERY = "DELETE FROM %s WHERE id = ?1";
//
//    protected final Class<T> entityBeanType;
//
//    @PersistenceContext
//    protected final EntityManager entityManager;
//
//    @SuppressWarnings("unchecked")
//    public AbstractRepository(EntityManager entityManager) {
//        this.entityManager = entityManager;
//        this.entityBeanType = ((Class<T>) ((ParameterizedType) getClass()
//                .getGenericSuperclass()).getActualTypeArguments()[0]);
//    }
//
//    /**
//     * Creates new instance in db
//     *
//     * @param model instance to create
//     * @return created instance with generated id
//     */
//    @Override
//    @Transactional
//    public T create(T model) {
//        entityManager.persist(model);
//        flushAndClear();
//        return model;
//    }
//
//    /**
//     * Finds db instance with provided id
//     *
//     * @param id id of instance to find
//     * @return Optional.empty if not found, Optional of instance if found
//     */
//    @Override
//    public Optional<T> findById(long id) {
//        return Optional.ofNullable(entityManager.find(entityBeanType, id));
//    }
//
//    /**
//     * Finds all instances in db
//     *
//     * @param limiter query limiter
//     * @return list of instances
//     */
//    @Override
//    public List<T> findAll(Limiter limiter) {
//        return entityManager
//                .createQuery(fillEntityClassInQuery(FIND_ALL_QUERY), entityBeanType)
//                .setFirstResult(limiter.getOffset())
//                .setMaxResults(limiter.getLimit())
//                .getResultList();
//    }
//
//    /**
//     * Deletes instance in db with specified id
//     *
//     * @param id id of instance to delete
//     * @return true if deleted, otherwise — false
//     * @throws WiredEntityDeletionException if entity is wired with another(s) in db tables
//     */
//    @Override
//    @Transactional
//    public boolean delete(long id) {
//        try {
//            boolean deleted = entityManager.createQuery(fillEntityClassInQuery(DELETE_BY_ID_QUERY))
//                                           .setParameter(1, id)
//                                           .executeUpdate() == 1;
//            flushAndClear();
//            return deleted;
//        } catch (PersistenceException ex) {
//            throw new WiredEntityDeletionException(entityBeanType, NamedModel_.ID, id);
//        }
//    }
//
//    protected Optional<T> singleParamQuery(String sqlQuery, Object columnValue) {
//        String queryWithEntityClass = fillEntityClassInQuery(sqlQuery);
//        try {
//            return Optional.of(
//                    entityManager.createQuery(queryWithEntityClass, entityBeanType)
//                                 .setParameter(1, columnValue)
//                                 .getSingleResult());
//        } catch (NoResultException ex) {
//            return Optional.empty();
//        }
//    }
//
//    protected List<T> listSingleParamQuery(String sqlQuery, Object columnValue, Limiter limiter) {
//        String queryWithEntityClass = fillEntityClassInQuery(sqlQuery);
//        return entityManager.createQuery(queryWithEntityClass, entityBeanType)
//                            .setParameter(1, columnValue)
//                            .setFirstResult(limiter.getOffset())
//                            .setMaxResults(limiter.getLimit())
//                            .getResultList();
//    }
//
//    /**
//     * Flushes and clears persistent context
//     */
//    @Override
//    public void flushAndClear() {
//        flush();
//        clear();
//    }
//
//    /**
//     * Flushes persistent context
//     */
//    @Override
//    public void flush() {
//        entityManager.flush();
//    }
//
//    /**
//     * Clears persistent context
//     */
//    @Override
//    public void clear() {
//        entityManager.clear();
//    }
//
//    protected String fillEntityClassInQuery(String query) {
//        return String.format(query, entityBeanType.getSimpleName());
//    }
//
//}
