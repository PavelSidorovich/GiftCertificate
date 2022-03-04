package com.epam.esm.gcs.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.epam.esm.gcs")
@EnableJpaRepositories("com.epam.esm.gcs")
@EnableJpaAuditing
@ComponentScan("com.epam.esm.gcs")
public class TestConfig {
}
