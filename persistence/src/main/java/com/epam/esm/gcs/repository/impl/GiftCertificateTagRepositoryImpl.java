package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.GiftCertificateTagRepository;
import com.epam.esm.gcs.repository.extractor.MultipleCertificateTagSetExtractor;
import com.epam.esm.gcs.repository.extractor.SingleCertificateTagSetExtractor;
import com.epam.esm.gcs.repository.mapper.GiftCertificateIdRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GiftCertificateTagRepositoryImpl implements GiftCertificateTagRepository {

    private static final String GIFT_CERTIFICATES_BY_TAGS_TABLE_NAME = "gift_certificates_by_tags";
    private static final String ID_COLUMN_NAME = "id";
    private static final String GIFT_CERTIFICATE_ID_COLUMN_NAME = "gift_certificate_id";
    private static final String TAG_ID_COLUMN_NAME = "tag_id";
    private static final String SELECT_QUERY =
            "SELECT tag.id as tag_id, tag.name as tag_name, gift_certificate.id as gift_certificate_id, " +
            "gift_certificate.name as gift_certificate_name, gift_certificate.description as description, " +
            "gift_certificate.price as price, gift_certificate.duration as duration, " +
            "gift_certificate.create_date as create_date, gift_certificate.last_update_date as last_update_date " +
            "FROM gift_certificates_by_tags " +
            "RIGHT JOIN tag ON tag_id = tag.id " +
            "RIGHT JOIN gift_certificate ON gift_certificate_id = gift_certificate.id ";
    private static final String FIND_CERTIFICATE_BY_ID_QUERY =
            SELECT_QUERY + "WHERE gift_certificate.id = ?";
    private static final String FIND_CERTIFICATE_BY_NAME_QUERY =
            SELECT_QUERY + "WHERE gift_certificate.name = ?";
    private static final String DELETE_ROWS_WITH_CERTIFICATE_ID = "DELETE FROM gift_certificates_by_tags " +
                                                                  "WHERE gift_certificate_id = ?";
    private static final String FILTER_FUNCTION = "SELECT _gift_certificate_id as id " +
                                                  "FROM \"find_certificates_by_filter\"(?, ?, ?);";

    private static final int INITIAL_CAPACITY = 2;
    private static final String LIKE_EXPRESSION = "%";

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;
    private final SingleCertificateTagSetExtractor singleCertificateExtractor;
    private final MultipleCertificateTagSetExtractor multipleCertificateExtractor;
    private final GiftCertificateIdRowMapper idRowMapper;

    @Autowired
    public GiftCertificateTagRepositoryImpl(DataSource dataSource,
                                            SingleCertificateTagSetExtractor singleCertificateExtractor,
                                            MultipleCertificateTagSetExtractor multipleCertificateExtractor,
                                            GiftCertificateIdRowMapper idRowMapper) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(GIFT_CERTIFICATES_BY_TAGS_TABLE_NAME)
                .usingGeneratedKeyColumns(ID_COLUMN_NAME)
                .usingColumns(GIFT_CERTIFICATE_ID_COLUMN_NAME, TAG_ID_COLUMN_NAME);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.singleCertificateExtractor = singleCertificateExtractor;
        this.multipleCertificateExtractor = multipleCertificateExtractor;
        this.idRowMapper = idRowMapper;
    }

    @Override
    public void link(GiftCertificateModel certificate) {
        for (TagModel tag : certificate.getTags()) {
            final Map<String, Object> columnsByValues = new HashMap<>(INITIAL_CAPACITY);

            columnsByValues.put(GIFT_CERTIFICATE_ID_COLUMN_NAME, certificate.getId());
            columnsByValues.put(TAG_ID_COLUMN_NAME, tag.getId());
            jdbcInsert.execute(columnsByValues);
        }
    }

    @Override
    public void unlink(GiftCertificateModel certificate) {
        jdbcTemplate.update(DELETE_ROWS_WITH_CERTIFICATE_ID, certificate.getId());
    }

    @Override
    public Optional<GiftCertificateModel> findById(long id) {
        return singleParamQuery(FIND_CERTIFICATE_BY_ID_QUERY, id);
    }

    @Override
    public Optional<GiftCertificateModel> findByName(String name) {
        return singleParamQuery(FIND_CERTIFICATE_BY_NAME_QUERY, name);
    }

    @Override
    public List<Long> findIdsByFilter(GiftCertificateModel certificate) {
        final List<TagModel> tags = certificate.getTags();
        final String certificateName = buildLikeExpression(certificate.getName());
        final String description = buildLikeExpression(certificate.getDescription());
        String tagName = null;
        if (!tags.isEmpty()) {
            tagName = tags.get(0).getName();
        }

        return jdbcTemplate.query(FILTER_FUNCTION, idRowMapper, tagName, certificateName, description);
    }

    private String buildLikeExpression(String param) {
        if (param != null) {
            return LIKE_EXPRESSION + param + LIKE_EXPRESSION;
        }
        return null;
    }

    @Override
    public List<GiftCertificateModel> findAll() {
        return jdbcTemplate.query(SELECT_QUERY, multipleCertificateExtractor);
    }

    private Optional<GiftCertificateModel> singleParamQuery(String sql, Object param) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(sql, singleCertificateExtractor, param)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
