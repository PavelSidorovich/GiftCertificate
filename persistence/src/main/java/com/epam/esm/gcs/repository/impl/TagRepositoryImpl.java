package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.mapper.TagColumn;
import com.epam.esm.gcs.mapper.TagRowMapper;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private static final String FIND_ALL_QUERY = "SELECT id as id, name as name FROM tag";
    private static final String FIND_BY_ID_QUERY = "SELECT id as id, name as name FROM tag WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT id as id, name as name FROM tag WHERE name = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM tag WHERE id = ?";

    private static final int INITIAL_MAP_CAPACITY = 1;

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;
    private final TagRowMapper tagRowMapper;

    @Autowired
    public TagRepositoryImpl(DataSource dataSource, TagRowMapper tagRowMapper) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TagColumn.getModelName())
                .usingGeneratedKeyColumns(TagColumn.ID.getColumnName())
                .usingColumns(TagColumn.NAME.getColumnName());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tagRowMapper = tagRowMapper;
    }

    @Override
    public TagModel create(TagModel model) {
        Map<String, Object> columnsByValues = new HashMap<>(INITIAL_MAP_CAPACITY);

        columnsByValues.put(TagColumn.NAME.getColumnName(), model.getName());
        model.setId(jdbcInsert.executeAndReturnKey(columnsByValues).longValue());

        return model;
    }

    @Override
    public Optional<TagModel> findById(long id) {
        return singleParamQuery(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<TagModel> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, tagRowMapper);
    }

    @Override
    public boolean delete(long id) {
        return jdbcTemplate.update(DELETE_BY_ID_QUERY, id) == 1;
    }

    @Override
    public boolean existsWithName(String name) {
        try {
            jdbcTemplate.queryForObject(FIND_BY_NAME_QUERY, tagRowMapper, name);

            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Optional<TagModel> findByName(String name) {
        return singleParamQuery(FIND_BY_NAME_QUERY, name);
    }

    private Optional<TagModel> singleParamQuery(String sqlQuery, Object columnValue){
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlQuery, tagRowMapper, columnValue)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
