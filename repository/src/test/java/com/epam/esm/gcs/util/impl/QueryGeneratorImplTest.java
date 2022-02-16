package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class QueryGeneratorImplTest {

    private final QueryGeneratorImpl queryGenerator;

    @Autowired
    public QueryGeneratorImplTest(QueryGeneratorImpl queryGenerator) {
        this.queryGenerator = queryGenerator;
    }

    @Test
    void select_shouldReturnSqlGenerator_always() {
        final String expected = "SELECT c ";
        final String actual = queryGenerator.select("c").getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void selectDistinct_shouldReturnSqlGenerator_always() {
        final String expected = "SELECT DISTINCT c ";
        final String actual = queryGenerator.selectDistinct("c").getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void delete_shouldReturnSqlGenerator_always() {
        final String expected = "DELETE ";
        final String actual = queryGenerator.delete().getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void where_shouldReturnSqlGenerator_always() {
        final String expected = "WHERE 1 = 1 ";
        final String actual = queryGenerator.where().getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void and_shouldReturnSqlGenerator_whenOrdinaryEquality() {
        final String expected = "AND col = val ";
        final String actual = queryGenerator.and("col", "val", false).getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void and_shouldReturnSqlGenerator_whenLikeExpression() {
        final String expected = "AND col LIKE val ";
        final String actual = queryGenerator.and("col", "val", true).getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void or_shouldReturnSqlGenerator_whenOrdinaryEquality() {
        final String expected = "OR col = val ";
        final String actual = queryGenerator.or("col", "val", false).getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void or_shouldReturnSqlGenerator_whenLikeExpression() {
        final String expected = "OR col LIKE val ";
        final String actual = queryGenerator.or("col", "val", true).getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void from_shouldReturnSqlGenerator_whenEntityNotSet() {
        final String expected = "FROM table e ";
        final String actual = queryGenerator.from("table").getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void from_shouldReturnSqlGenerator_whenEntitySet() {
        final String expected = "SELECT c FROM table c ";
        final String actual = queryGenerator.select("c")
                                            .from("table")
                                            .getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void orderBy_shouldReturnSqlGenerator_whenHasColumnsToSort() {
        final String expected = "ORDER BY col4 DESC, col5 DESC, col2 ASC, col1 ASC";
        HashMap<String, String> sortTypeByColumn = new HashMap<>();
        sortTypeByColumn.put("col1", "ASC");
        sortTypeByColumn.put("col2", "ASC");
        sortTypeByColumn.put("col5", "DESC");
        sortTypeByColumn.put("col3", "NONE");
        sortTypeByColumn.put("col4", "DESC");

        final String actual = queryGenerator.orderBy(sortTypeByColumn)
                                            .getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void orderBy_shouldReturnSqlGenerator_whenHasNoColumnsToSort() {
        HashMap<String, String> sortTypeByColumn = new HashMap<>();
        final String expected = "";
        final String actual = queryGenerator.orderBy(sortTypeByColumn)
                                            .getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void join_shouldReturnSqlGenerator_always() {
        final String expected = "JOIN table AS c ";
        final String actual = queryGenerator.join("table", "c")
                                            .getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void leftJoin_shouldReturnSqlGenerator_always() {
        final String expected = "LEFT JOIN table AS c ";
        final String actual = queryGenerator.leftJoin("table", "c")
                                            .getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void getSqlQuery_shouldReturnSqlQuery_always() {
        final String expected = "SELECT c FROM table1 c LEFT JOIN table2 AS t ";
        final String actual = queryGenerator.select("c")
                                            .from("table1")
                                            .leftJoin("table2", "t")
                                            .getSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void reset_shouldResetQueryGenerator_always() {
        final String expected = "SELECT c FROM table1 c LEFT JOIN table2 AS t ";
        final String actual = queryGenerator.select("c")
                                            .from("table1")
                                            .leftJoin("table2", "t")
                                            .getSqlQuery();
        assertEquals(expected, actual);

        queryGenerator.reset();

        assertTrue(queryGenerator.getSqlQuery().isEmpty());
    }

}