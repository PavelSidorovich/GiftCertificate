package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.repository.mapper.GiftCertificateColumn;
import com.epam.esm.gcs.repository.mapper.GiftCertificateRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.gcs.repository.mapper.GiftCertificateColumn.*;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private static final String FIND_ALL_QUERY =
            "SELECT id as id, name as name, description as description," +
            " price as price, duration as duration, create_date as create_date," +
            " last_update_date as last_update_date FROM gift_certificate";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = FIND_ALL_QUERY + " WHERE name = ?";
    private static final String UPDATE_QUERY = "UPDATE gift_certificate " +
                                               "SET name = COALESCE(?, name), " +
                                               "description = COALESCE(?, description), " +
                                               "price = COALESCE(?, price), " +
                                               "duration = COALESCE(?, duration), " +
                                               "last_update_date = ? " +
                                               "WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM gift_certificate WHERE id = ?";

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;
    private final GiftCertificateRowMapper certificateRowMapper;

    @Autowired
    public GiftCertificateRepositoryImpl(DataSource dataSource, GiftCertificateRowMapper certificateRowMapper) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(GiftCertificateColumn.getModelName())
                .usingGeneratedKeyColumns(GiftCertificateColumn.ID.getColumnName())
                .usingColumns(GiftCertificateColumn.getColumnNames());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.certificateRowMapper = certificateRowMapper;
    }

    @Override
    public GiftCertificateModel create(GiftCertificateModel model) {
        Map<String, Object> columnsByValues = new HashMap<>();
        LocalDateTime currentTime = LocalDateTime.now();

        columnsByValues.put(NAME.getColumnName(), model.getName());
        columnsByValues.put(DESCRIPTION.getColumnName(), model.getDescription());
        columnsByValues.put(PRICE.getColumnName(), model.getPrice());
        columnsByValues.put(DURATION.getColumnName(), model.getDuration());
        columnsByValues.put(CREATE_DATE.getColumnName(), currentTime);
        columnsByValues.put(LAST_UPDATE_DATE.getColumnName(), currentTime);
        model.setId(jdbcInsert.executeAndReturnKey(columnsByValues).longValue());
        model.setCreateDate(currentTime);
        model.setLastUpdateDate(currentTime);

        return model;
    }

    @Override
    public Optional<GiftCertificateModel> findById(long id) {
        return singleParamQuery(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<GiftCertificateModel> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, certificateRowMapper);
    }

    @Override
    public boolean delete(long id) {
        return jdbcTemplate.update(DELETE_BY_ID_QUERY, id) == 1;
    }

    @Override
    public Optional<GiftCertificateModel> update(GiftCertificateModel model) {
        Optional<GiftCertificateModel> oldCertificate = findById(model.getId());

        jdbcTemplate.update(
                UPDATE_QUERY, model.getName(), model.getDescription(), model.getPrice(),
                model.getDuration(), LocalDateTime.now(), model.getId()
        );
        return oldCertificate;
    }

    @Override
    public boolean existsWithName(String name) {
        try {
            jdbcTemplate.queryForObject(FIND_BY_NAME_QUERY, certificateRowMapper, name);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Optional<GiftCertificateModel> findByName(String name) {
        return singleParamQuery(FIND_BY_NAME_QUERY, name);
    }

    private Optional<GiftCertificateModel> singleParamQuery(String sqlQuery, Object columnValue) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlQuery, certificateRowMapper, columnValue)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
