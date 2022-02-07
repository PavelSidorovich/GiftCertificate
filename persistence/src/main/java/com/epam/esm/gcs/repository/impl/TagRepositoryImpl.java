package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import com.epam.esm.gcs.repository.mapper.TagColumn;
import com.epam.esm.gcs.repository.mapper.TagRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TagRepositoryImpl
        extends AbstractRepository<TagModel> implements TagRepository {

    private static final String FIND_ALL_QUERY = "SELECT id as id, name as name FROM tag";
    private static final String FIND_BY_ID_QUERY = "SELECT id as id, name as name FROM tag WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT id as id, name as name FROM tag WHERE name = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM tag WHERE id = ?";

    private static final int INITIAL_MAP_CAPACITY = 1;

    private final SimpleJdbcInsert jdbcInsert;

    public TagRepositoryImpl(DataSource dataSource, TagRowMapper tagRowMapper) {
        super(dataSource, tagRowMapper);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TagColumn.getModelName())
                .usingGeneratedKeyColumns(TagColumn.ID.getColumnName())
                .usingColumns(TagColumn.NAME.getColumnName());
    }

    /**
     * Creates new tag
     *
     * @param model tag to create
     * @return created tag with generated id
     */
    @Override
    public TagModel create(TagModel model) {
        Map<String, Object> columnsByValues = new HashMap<>(INITIAL_MAP_CAPACITY);

        columnsByValues.put(TagColumn.NAME.getColumnName(), model.getName());
        model.setId(jdbcInsert.executeAndReturnKey(columnsByValues).longValue());

        return model;
    }

    /**
     * Finds tag with provided id
     *
     * @param id id of tag to find
     * @return Optional.empty if not found, Optional of tag if found
     */
    @Override
    public Optional<TagModel> findById(long id) {
        return singleParamQuery(FIND_BY_ID_QUERY, id);
    }

    /**
     * Finds all tags
     *
     * @return list of tags
     */
    @Override
    public List<TagModel> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, rowMapper);
    }

    /**
     * Deletes tag with specified id
     *
     * @param id id of tag to delete
     * @return true if deleted, otherwise - false
     */
    @Override
    public boolean delete(long id) {
        return jdbcTemplate.update(DELETE_BY_ID_QUERY, id) == 1;
    }

    /**
     * Checks if tag with specified name exists
     *
     * @param name name of tag check for existence
     * @return true if exists, otherwise - false
     */
    @Override
    public boolean existsWithName(String name) {
        try {
            jdbcTemplate.queryForObject(FIND_BY_NAME_QUERY, rowMapper, name);

            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
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
