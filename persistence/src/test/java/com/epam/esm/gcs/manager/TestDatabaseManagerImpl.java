package com.epam.esm.gcs.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class TestDatabaseManagerImpl implements TestDatabaseManager {

    private static final String DB_SCHEMA_SQL_PATH = "/db/schema.sql";
    private static final String DB_TEST_DATA_SQL_PATH = "/db/test-data.sql";
    private static final String[] TABLE_NAMES = { "tag", "gift_certificate", "gift_certificates_by_tags" };

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TestDatabaseManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void cleanAndPopulateTables() throws SQLException {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLE_NAMES);
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new ClassPathResource(DB_TEST_DATA_SQL_PATH)
        );
    }

}