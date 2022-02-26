package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public class TagRepositoryImpl
        extends AbstractRepository<TagModel>
        implements TagRepository {

    private static final String FIND_BY_NAME_QUERY = "SELECT t FROM %s t WHERE t.name = lower(?1)";

    public TagRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Checks if tag with specified name exists
     *
     * @param name name of tag check for existence
     * @return true if exists, otherwise - false
     */
    @Override
    public boolean existsWithName(String name) {
        return findByName(name).isPresent();
    }

    /**
     * Finds tag with specified name
     *
     * @param name name of tag to find
     * @return Optional.empty if not found, tag if found
     */
    @Override
    public Optional<TagModel> findByName(String name) {
        return singleParamQuery(FIND_BY_NAME_QUERY, name);
    }

}
