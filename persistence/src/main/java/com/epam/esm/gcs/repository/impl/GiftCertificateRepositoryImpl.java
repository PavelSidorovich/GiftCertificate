package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.repository.mapper.GiftCertificateColumn;
import com.epam.esm.gcs.repository.mapper.GiftCertificateRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class GiftCertificateRepositoryImpl
        extends AbstractRepository<GiftCertificateModel> implements GiftCertificateRepository {

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

    @Autowired
    public GiftCertificateRepositoryImpl(DataSource dataSource, GiftCertificateRowMapper certificateRowMapper) {
        super(dataSource, certificateRowMapper);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(GiftCertificateColumn.getModelName())
                .usingGeneratedKeyColumns(GiftCertificateColumn.ID.getColumnName())
                .usingColumns(GiftCertificateColumn.getColumnNames());
    }

    /**
     * Creates new certificate
     * @param model certificate to create
     * @return created certificate with generated id
     */
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

    /**
     * Finds certificate with provided id
     * @param id id of certificate to find
     * @return Optional.empty if not found, Optional of certificate if found
     */
    @Override
    public Optional<GiftCertificateModel> findById(long id) {
        return singleParamQuery(FIND_BY_ID_QUERY, id);
    }

    /**
     * Finds all certificates
     * @return list of certificates
     */
    @Override
    public List<GiftCertificateModel> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, rowMapper);
    }

    /**
     * Deletes certificate with specified id
     * @param id id of certificate to delete
     * @return true if deleted, otherwise - false
     */
    @Override
    public boolean delete(long id) {
        return jdbcTemplate.update(DELETE_BY_ID_QUERY, id) == 1;
    }

    /**
     * Updates certificate with specified id
     * @param model certificate to update. Should contain id
     * @return old version of certificate or Optional.empty if not found
     */
    @Override
    public Optional<GiftCertificateModel> update(GiftCertificateModel model) {
        Optional<GiftCertificateModel> oldCertificate = findById(model.getId());

        jdbcTemplate.update(
                UPDATE_QUERY, model.getName(), model.getDescription(), model.getPrice(),
                model.getDuration(), LocalDateTime.now(), model.getId()
        );
        return oldCertificate;
    }


    /**
     * Checks if certificate with specified name exists
     * @param name name of certificate check for existence
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
     * Finds certificate with specified name
     * @param name name of certificate to find
     * @return Optional.empty if not found, certificate if found
     */
    @Override
    public Optional<GiftCertificateModel> findByName(String name) {
        return singleParamQuery(FIND_BY_NAME_QUERY, name);
    }

}
