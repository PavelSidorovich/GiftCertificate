package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.filter.GiftCertificateFilter;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.util.FilterQueryGenerator;
import com.epam.esm.gcs.util.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GiftCertificateFilterQueryGenerator
        implements FilterQueryGenerator<GiftCertificateFilter> {

    private static final String JPA_ENTITY_NAME = GiftCertificateModel.class.getSimpleName();
    private static final String LIKE_PARAMETER = "'%%%s%%'";
    private static final String DEFAULT_PARAMETER = "'%s'";
    private static final String C_CREATE_DATE = "c.createDate";
    private static final String C_NAME = "c.name";
    private static final String C_DESCRIPTION = "c.description";
    private static final String T_NAME = "t.name";
    private static final String C_TAGS = "c.tags";
    private static final String T_ENTITY = "t";
    private static final String C_ENTITY = "c";
    private static final String LOWER = "lower(%s)";

    private QueryGenerator queryGenerator;

    @Autowired
    public void setQueryGenerator(QueryGenerator queryGenerator) {
        this.queryGenerator = queryGenerator;
    }

    @Override
    public String getSqlQuery(GiftCertificateFilter filter) {
        Map<String, String> sortTypeByColumn = new LinkedHashMap<>();
        queryGenerator.selectDistinct(C_ENTITY)
                      .from(JPA_ENTITY_NAME)
                      .leftJoin(C_TAGS, T_ENTITY)
                      .where();
        filterFieldIsAdded(T_NAME, filter.getTagName(),
                           false, false, true);
        filterFieldIsAdded(C_NAME, filter.getCertificateName(),
                           false, true, true);
        filterFieldIsAdded(C_DESCRIPTION, filter.getDescription(),
                           false, true, true);
        sortTypeByColumn.put(C_NAME, filter.getSortNameType().name());
        sortTypeByColumn.put(C_CREATE_DATE, filter.getSortCreateDateType().name());
        queryGenerator.orderBy(sortTypeByColumn);

        return queryGenerator.getSqlQuery();
    }

    private void filterFieldIsAdded(String column, String value, boolean orExpression,
                                    boolean likeExpression, boolean ignoreCase) {
        if (value != null) {
            String colValue = likeExpression
                    ? String.format(LIKE_PARAMETER, value)
                    : String.format(DEFAULT_PARAMETER, value);
            column = ignoreCase? String.format(LOWER, column) : column;
            value = ignoreCase? String.format(LOWER, colValue) : colValue;
            if (orExpression) {
                queryGenerator.or(column, value, likeExpression);
            } else {
                queryGenerator.and(column, value, likeExpression);
            }
        }
    }

}
