package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.filter.GiftCertificateFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class GiftCertificateFilterQueryGeneratorTest {

    private final GiftCertificateFilterQueryGenerator filterQueryGenerator;

    @Autowired
    public GiftCertificateFilterQueryGeneratorTest(
            GiftCertificateFilterQueryGenerator filterQueryGenerator) {
        this.filterQueryGenerator = filterQueryGenerator;
    }

    @Test
    void getSqlQuery_shouldReturnFilteringSqlQuery_always() {
        final String expected1 = "SELECT DISTINCT c FROM GiftCertificateModel " +
                                 "c LEFT JOIN c.tags AS t WHERE 1 = 1 AND lower(t.name) = " +
                                 "lower('tagName') AND lower(c.name) LIKE lower('%certName%') " +
                                 "AND lower(c.description) LIKE lower('%desc%') ORDER BY c.name " +
                                 "DESC, c.createDate ASC";
        final String expected2 = "SELECT DISTINCT c FROM GiftCertificateModel c LEFT JOIN c.tags AS" +
                                 " t WHERE 1 = 1 AND lower(t.name) = lower('tagName') ORDER BY c.createDate ASC";
        final String expected3 = "SELECT DISTINCT c FROM GiftCertificateModel c LEFT JOIN c.tags AS t WHERE 1 = 1 ";
        final String expected4 = "SELECT DISTINCT c FROM GiftCertificateModel c LEFT JOIN c.tags AS t WHERE 1 = 1 " +
                                 "AND lower(t.name) = lower('tagName') ";
        final String expected5 = "SELECT DISTINCT c FROM GiftCertificateModel c LEFT JOIN c.tags AS t WHERE 1 = 1 " +
                                 "ORDER BY c.createDate ASC";

        String actual1 = filterQueryGenerator.getSqlQuery(getFilters().get(0));
        String actual2 = filterQueryGenerator.getSqlQuery(getFilters().get(1));
        String actual3 = filterQueryGenerator.getSqlQuery(getFilters().get(2));
        String actual4 = filterQueryGenerator.getSqlQuery(getFilters().get(3));
        String actual5 = filterQueryGenerator.getSqlQuery(getFilters().get(4));

        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
        assertEquals(expected5, actual5);
    }

    private List<GiftCertificateFilter> getFilters() {
        return List.of(
                new GiftCertificateFilter(
                        "certName", "tagName", "desc", "ASC", "DESC"
                ),
                new GiftCertificateFilter(
                        null, "tagName", null, "ASC", "NONE"
                ),
                new GiftCertificateFilter(
                        null, null, null, null, null
                ),
                new GiftCertificateFilter(
                        null, "tagName", null, null, "NONE"
                ),
                new GiftCertificateFilter(
                        null, null, null, "ASC", null
                )
        );
    }

}
