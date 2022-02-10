package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.repository.CrdRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T> implements CrdRepository<T> {

    private static final String FIND_ALL_QUERY = "SELECT m FROM %s m";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM %s WHERE id = ?1";

    protected final Class<T> entityBeanType;

    @PersistenceContext
    protected final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public AbstractRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityBeanType = ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * Creates new instance in db
     *
     * @param model instance to create
     * @return created instance with generated id
     */
    @Override
    @Transactional
    public T create(T model) {
        entityManager.persist(model);
        flushAndClear();
        return model;
    }

    /**
     * Finds db instance with provided id
     *
     * @param id id of instance to find
     * @return Optional.empty if not found, Optional of instance if found
     */
    @Override
    public Optional<T> findById(long id) {
        return Optional.ofNullable(entityManager.find(entityBeanType, id));
    }

    /**
     * Finds all instances in db
     *
     * @return list of instances
     */
    @Override
    @Transactional
    public List<T> findAll() {
        return entityManager
                .createQuery(fillEntityClassInQuery(FIND_ALL_QUERY), entityBeanType)
                .getResultList();
    }

    /**
     * Deletes instance in db with specified id
     *
     * @param id id of instance to delete
     * @return true if deleted, otherwise — false
     */
    @Override
    @Transactional
    public boolean delete(long id) {
        boolean deleted = entityManager.createQuery(fillEntityClassInQuery(DELETE_BY_ID_QUERY))
                                       .setParameter(1, id)
                                       .executeUpdate() == 1;
        flushAndClear();
        return deleted;
    }

    protected Optional<T> singleParamQuery(String sqlQuery, Object columnValue) {
        String queryWithEntityClass = fillEntityClassInQuery(sqlQuery);
        try {
            return Optional.of(
                    entityManager.createQuery(queryWithEntityClass, entityBeanType)
                                 .setParameter(1, columnValue)
                                 .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Flushes and clears persistent context
     */
    public void flushAndClear() {
        flush();
        clear();
    }

    /**
     * Flushes persistent context
     */
    public void flush() {
        entityManager.flush();
    }

    /**
     * Cleares persistent context
     */
    public void clear() {
        entityManager.clear();
    }

    private String fillEntityClassInQuery(String query) {
        return String.format(query, entityBeanType.getSimpleName());
    }

}
