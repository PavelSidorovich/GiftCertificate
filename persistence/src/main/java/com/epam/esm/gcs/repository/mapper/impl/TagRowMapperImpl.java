package com.epam.esm.gcs.repository.mapper.impl;

import com.epam.esm.gcs.repository.mapper.TagColumn;
import com.epam.esm.gcs.repository.mapper.TagRowMapper;
import com.epam.esm.gcs.model.TagModel;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TagRowMapperImpl implements TagRowMapper {

    @Override
    public TagModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TagModel(
                rs.getLong(TagColumn.ID.getColumnName()),
                rs.getString(TagColumn.NAME.getColumnName())
        );
    }

}
