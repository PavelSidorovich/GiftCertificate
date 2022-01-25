package com.epam.esm.gcs.repository.extractor.impl;

import com.epam.esm.gcs.repository.mapper.GiftCertificateColumn;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.extractor.SingleCertificateTagSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class SingleCertificateTagSetExtractorImpl implements SingleCertificateTagSetExtractor {

    private static final String GIFT_CERTIFICATE_ID_COLUMN_NAME = "gift_certificate_id";
    private static final String GIFT_CERTIFICATE_NAME_COLUMN_NAME = "gift_certificate_name";
    private static final String TAG_ID_COLUMN_NAME = "tag_id";
    private static final String TAG_NAME_COLUMN_NAME = "tag_name";

    @Override
    public GiftCertificateModel extractData(ResultSet rs) throws SQLException, DataAccessException {
        boolean certificateCreated = false;
        GiftCertificateModel certificate = null;

        while (rs.next()) {
            final String tagName = rs.getString(TAG_NAME_COLUMN_NAME);
            if (!certificateCreated) {
                certificate = buildCertificate(rs);
                certificateCreated = true;
            }
            if (tagName != null) {
                certificate.getTags().add(new TagModel(rs.getLong(TAG_ID_COLUMN_NAME), tagName));
            }
        }
        return certificate;
    }

    private GiftCertificateModel buildCertificate(ResultSet rs) throws SQLException {
        return GiftCertificateModel
                .builder()
                .id(rs.getLong(GIFT_CERTIFICATE_ID_COLUMN_NAME))
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
