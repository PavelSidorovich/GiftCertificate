package com.epam.esm.gcs.repository.mapper.impl;

import com.epam.esm.gcs.repository.mapper.GiftCertificateColumn;
import com.epam.esm.gcs.repository.mapper.GiftCertificateIdRowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GiftCertificateIdRowMapperImpl implements GiftCertificateIdRowMapper {

    @Override
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(GiftCertificateColumn.ID.getColumnName());
    }

}
