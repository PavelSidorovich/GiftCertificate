package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.filter.GiftCertificateFilter;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.util.FilterQueryGenerator;
import com.epam.esm.gcs.util.QueryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GiftCertificateFilterQueryGenerator
        implements FilterQueryGenerator<GiftCertificateFilter> {

    private static final String JPA_ENTITY_NAME = GiftCertificateModel.class.getSimpleName();
    private static final String LIKE_PARAMETER = "'%%%s%%'";
    private static final String DEFAULT_PARAMETER = "'%s'";

    String q = "SELECT c FROM GiftCertificateModel c JOIN c.tags as t WHERE " +
               "t.name = 'pleasure' AND lower(c.name) LIKE lower('%fo%') AND c.description LIKE '%%' " +
               "ORDER BY c.createDate, c.name DESC ";

    private QueryGenerator queryGenerator;

    @Autowired
    public void setQueryGenerator(QueryGenerator queryGenerator) {
        this.queryGenerator = queryGenerator;
    }

    @Override
    public String getSqlQuery(GiftCertificateFilter filter) {
        Map<String, String> sortTypeByColumn = new LinkedHashMap<>();
        queryGenerator.selectDistinct("c")
                      .from(JPA_ENTITY_NAME)
                      .leftJoin("c.tags", "t")
                      .where();
        filterFieldIsAdded("t.name", filter.getTagName(),
                           false, false, true);
        filterFieldIsAdded("c.name", filter.getCertificateName(),
                           false, true, true);
        filterFieldIsAdded("c.description", filter.getDescription(),
                           false, true, true);
        sortTypeByColumn.put("c.name", filter.getSortNameType().name());
        sortTypeByColumn.put("c.createDate", filter.getSortCreateDateType().name());
        queryGenerator.orderBy(sortTypeByColumn);

        return queryGenerator.getSqlQuery();
    }

    private void filterFieldIsAdded(String column, String value, boolean orExpression,
                                    boolean likeExpression, boolean ignoreCase) {
        if (value != null) {
            String colValue = likeExpression
                    ? String.format(LIKE_PARAMETER, value)
                    : String.format(DEFAULT_PARAMETER, value);
            column = ignoreCase? String.format("lower(%s)", column) : column;
            value = ignoreCase? String.format("lower(%s)", colValue) : colValue;
            if (orExpression) {
                queryGenerator.or(column, value, likeExpression);
            } else {
                queryGenerator.and(column, value, likeExpression);
            }
        }
    }

}
