package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.util.QueryGenerator;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Map.Entry;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QueryGeneratorImpl implements QueryGenerator {

    private static final String SELECT = "SELECT ";
    private static final String DELETE = "DELETE ";
    private static final String FROM = "FROM ";
    private static final String WHERE = "WHERE ";
    private static final String AND = "AND ";
    private static final String OR = "OR ";
    private static final String COMMA = ", ";
    private static final String JOIN = "JOIN ";
    private static final String EQUALS = "= ";
    private static final String LIKE = "LIKE ";
    private static final String ORDER_BY = "ORDER BY ";
    private static final String WHITE_SPACE = " ";
    private static final String ASC_SORT = "ASC ";
    private static final String DESC_SORT = "DESC ";
    private static final String ALL_CONDITION = "1 = 1";
    private static final String AS = "AS ";
    private static final String DISTINCT = "DISTINCT ";
    private static final String LEFT = "LEFT ";

    private String sqlQuery;
    private String jpaEntity;

    public QueryGeneratorImpl() {
        this.sqlQuery = "";
        this.jpaEntity = "e";
    }

    @Override
    public QueryGenerator select(String jpaName) {
        reset();
        jpaEntity = jpaName;
        sqlQuery += SELECT
                    + jpaEntity
                    + WHITE_SPACE;
        return this;
    }

    @Override
    public QueryGenerator selectDistinct(String jpaName) {
        reset();
        jpaEntity = jpaName;
        sqlQuery += SELECT
                    + DISTINCT
                    + jpaEntity
                    + WHITE_SPACE;
        return this;
    }

    @Override
    public QueryGenerator delete() {
        reset();
        sqlQuery += DELETE;
        return this;
    }

    @Override
    public QueryGenerator where() {
        sqlQuery += WHERE + ALL_CONDITION + WHITE_SPACE;
        return this;
    }

    @Override
    public QueryGenerator and(String column, String value, boolean like) {
        sqlQuery += AND;
        return addCondition(column, value, like);
    }

    @Override
    public QueryGenerator or(String column, String value, boolean like) {
        sqlQuery += OR;
        return addCondition(column, value, like);
    }

    private QueryGenerator addCondition(String column, String value, boolean like) {
        sqlQuery += column
                    + WHITE_SPACE
                    + (like? LIKE : EQUALS)
                    + value
                    + WHITE_SPACE;
        return this;
    }

    @Override
    public QueryGenerator from(String jpaTable) {
        sqlQuery += FROM
                    + jpaTable
                    + WHITE_SPACE
                    + jpaEntity
                    + WHITE_SPACE;
        return this;
    }

    @Override
    public QueryGenerator orderBy(Map<String, String> typeByColumn) {
        final String trimmedAsc = ASC_SORT.trim();
        final String trimmedDesc = DESC_SORT.trim();

        addOrderByIfContainsSortType(typeByColumn, trimmedAsc, trimmedDesc);
        sqlQuery = addOrderByColumns(typeByColumn, trimmedAsc, trimmedDesc);

        return this;
    }

    private void addOrderByIfContainsSortType(
            Map<String, String> typeByColumn, String trimmedAsc, String trimmedDesc) {
        int orderConditionsSize = typeByColumn.size();
        if (orderConditionsSize > 0
            && (typeByColumn.containsValue(trimmedAsc)
                || typeByColumn.containsValue(trimmedDesc))) {
            sqlQuery += ORDER_BY;
        }
    }

    private String addOrderByColumns(
            Map<String, String> typeByColumn, String trimmedAsc, String trimmedDesc) {
        int currentCondition = 1;
        StringBuilder sb = new StringBuilder(sqlQuery);
        for (Entry<String, String> sortTypeByColumn : typeByColumn.entrySet()) {
            String sortType = sortTypeByColumn.getValue();
            if (trimmedAsc.equalsIgnoreCase(sortType)
                || trimmedDesc.equalsIgnoreCase(sortType)) {
                if (currentCondition++ != 1) {
                    sb.append(COMMA);
                }
                sb.append(sortTypeByColumn.getKey())
                  .append(WHITE_SPACE)
                  .append(sortType);
            }
        }
        return sb.toString();
    }

    @Override
    public QueryGenerator join(String trgTable, String asEntity) {
        sqlQuery += JOIN
                    + trgTable
                    + WHITE_SPACE
                    + AS
                    + asEntity
                    + WHITE_SPACE;
        return this;
    }

    @Override
    public QueryGenerator leftJoin(String trgTable, String asEntity) {
        sqlQuery += LEFT;
        join(trgTable, asEntity);
        return this;
    }

    @Override
    public String getSqlQuery() {
        return sqlQuery;
    }

    @Override
    public void reset() {
        sqlQuery = "";
        jpaEntity = "e";
    }

}
