package com.epam.esm.gcs.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@PropertySource("classpath:/db/dataSource.properties")
public class DataSourceConfig {

    private static final String DRIVER_CLASS_NAME_PROPERTY_NAME = "dataSource.driverClassName";
    private static final String URL_PROPERTY_NAME = "dataSource.url";
    private static final String USER_NAME_PROPERTY_NAME = "dataSource.userName";
    private static final String CONNECTION_POOL_INITIAL_SIZE_PROPERTY_NAME = "connectionPool.initialSize";
    private static final String CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME = "connectionPool.maxSize";
    private static final String PASSWORD_PROPERTY_NAME = "dataSource.password";

    private final Environment env;

    @Autowired
    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Profile("dev")
    @Bean
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:/db/schema.sql")
                .addScript("classpath:/db/test-data.sql")
                .build();
    }

    @Profile("prod")
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(env.getProperty(DRIVER_CLASS_NAME_PROPERTY_NAME));
        dataSource.setUrl(env.getProperty(URL_PROPERTY_NAME));
        dataSource.setUsername(env.getProperty(USER_NAME_PROPERTY_NAME));
        dataSource.setPassword(env.getProperty(PASSWORD_PROPERTY_NAME));
        dataSource.setInitialSize(Integer.parseInt(
                Objects.requireNonNull(env.getProperty(CONNECTION_POOL_INITIAL_SIZE_PROPERTY_NAME)))
        );
        dataSource.setMaxTotal(Integer.parseInt(
                Objects.requireNonNull(env.getProperty(CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME)))
        );

        return dataSource;
    }

}
