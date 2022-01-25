package com.epam.esm.gcs.repository.extractor.impl;

import com.epam.esm.gcs.mapper.GiftCertificateColumn;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.extractor.MultipleCertificateTagSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MultipleCertificateTagSetExtractorImpl
        implements MultipleCertificateTagSetExtractor {

    private static final String GIFT_CERTIFICATE_ID_COLUMN_NAME = "gift_certificate_id";
    private static final String GIFT_CERTIFICATE_NAME_COLUMN_NAME = "gift_certificate_name";
    private static final String TAG_ID_COLUMN_NAME = "tag_id";
    private static final String TAG_NAME_COLUMN_NAME = "tag_name";

    @Override
    public List<GiftCertificateModel> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, GiftCertificateModel> certificatesById = new HashMap<>();

        while (rs.next()) {
            final long certificateId = rs.getLong(GIFT_CERTIFICATE_ID_COLUMN_NAME);
            final String tagName = rs.getString(TAG_NAME_COLUMN_NAME);
            GiftCertificateModel certificate = certificatesById.get(certificateId);
            if (certificate == null) {
                certificate = buildCertificate(rs, certificateId);
            }
            if (tagName != null) {
                certificate.getTags().add(new TagModel(rs.getLong(TAG_ID_COLUMN_NAME), tagName));
            }
            certificatesById.put(certificateId, certificate);
        }
        return new ArrayList<>(certificatesById.values());
    }

    private GiftCertificateModel buildCertificate(ResultSet rs, long certificateId) throws SQLException {
        return GiftCertificateModel
                .builder()
                .id(certificateId)
                .name(rs.getString(GIFT_CERTIFICATE_NAME_COLUMN_NAME))
                .description(rs.getString(GiftCertificateColumn.DESCRIPTION.getColumnName()))
                .price(rs.getBigDecimal(GiftCertificateColumn.PRICE.getColumnName()))
                .duration(rs.getInt(GiftCertificateColumn.DURATION.getColumnName()))
                .createDate(rs.getObject(GiftCertificateColumn.CREATE_DATE.getColumnName(),
                                         LocalDateTime.class))
                .lastUpdateDate(rs.getObject(GiftCertificateColumn.LAST_UPDATE_DATE.getColumnName(),
                                             LocalDateTime.class))
                .tags(new ArrayList<>())
                .build();
    }

}
