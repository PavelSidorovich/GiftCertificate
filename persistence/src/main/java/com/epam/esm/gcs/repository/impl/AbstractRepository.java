package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.repository.CrdRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.Optional;

public abstract class AbstractRepository<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    public AbstractRepository(DataSource dataSource, RowMapper<T> rowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.rowMapper = rowMapper;
    }

    protected Optional<T> singleParamQuery(String sqlQuery, Object columnValue) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlQuery, rowMapper, columnValue)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
