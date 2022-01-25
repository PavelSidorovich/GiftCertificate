package com.epam.esm.gcs.repository.mapper.impl;

import com.epam.esm.gcs.repository.mapper.GiftCertificateRowMapper;
import com.epam.esm.gcs.model.GiftCertificateModel;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.epam.esm.gcs.repository.mapper.GiftCertificateColumn.*;

@Component
public class GiftCertificateRowMapperImpl implements GiftCertificateRowMapper {

    @Override
    public GiftCertificateModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return GiftCertificateModel
                .builder()
                .id(rs.getLong(ID.getColumnName()))
                .name(rs.getString(NAME.getColumnName()))
                .description(rs.getString(DESCRIPTION.getColumnName()))
                .price(rs.getBigDecimal(PRICE.getColumnName()))
                .duration(rs.getInt(DURATION.getColumnName()))
                .createDate(rs.getObject(CREATE_DATE.getColumnName(), LocalDateTime.class))
                .lastUpdateDate(rs.getObject(LAST_UPDATE_DATE.getColumnName(), LocalDateTime.class))
                .tags(new ArrayList<>())
                .build();
    }

}
