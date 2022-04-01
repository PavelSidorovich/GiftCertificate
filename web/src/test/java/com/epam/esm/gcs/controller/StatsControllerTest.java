package com.epam.esm.gcs.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findTheMostUsedTag_shouldReturnStatus200_whenRoleAdminAndTagIsFound() {
        RestAssuredMockMvc
                .given()
                .when().get("/stats/tags")
                .then()
                .status(HttpStatus.OK)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo("gift"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findTheMostUsedTag_shouldReturnStatus403_whenRoleUser() {
        RestAssuredMockMvc
                .given()
                .when().get("/stats/tags")
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    @Transactional
    @Sql("classpath:sql/delete_all_orders.sql")
    @WithMockUser(roles = "ADMIN")
    void findTheMostUsedTag_shouldReturnStatus404_whenTagNotFound() {
        RestAssuredMockMvc
                .given()
                .when().get("/stats/tags")
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("responseCode", Matchers.equalTo("40402"))
                .body("message", Matchers.notNullValue());
    }

}