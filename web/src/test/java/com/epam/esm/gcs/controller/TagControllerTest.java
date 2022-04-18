package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.TagDto;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    static Stream<Arguments> paginationProvider() {
        return Stream.of(
                Arguments.of(0, 2, 2),
                Arguments.of(0, 10, 7),
                Arguments.of(0, 6, 6),
                Arguments.of(1, 4, 3),
                Arguments.of(-10, 10, 7),
                Arguments.of(-10, -10, 7),
                Arguments.of(10, -10, null)
        );
    }

    @ParameterizedTest
    @MethodSource("paginationProvider")
    @WithMockUser(roles = { "ADMIN", "USER" })
    void findAll_shouldReturnStatus200_whenRoleAdminOrUser(Integer page, Integer pageSize, Integer resultSize) {
        RestAssuredMockMvc
                .given()
                .param("page", page)
                .param("size", pageSize)
                .when().get("/tags")
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.tagDtoList.size()", Matchers.is(resultSize));
    }

    @ParameterizedTest
    @MethodSource("paginationProvider")
    void findAll_shouldReturnStatus401_whenUserNotUnauthorized(Integer page, Integer pageSize) {
        RestAssuredMockMvc
                .given()
                .param("page", page)
                .param("size", pageSize)
                .when().get("/tags")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest
    @CsvSource({ "1,gift", "7,learning" })
    @WithMockUser(roles = { "ADMIN", "USER" })
    void findById_shouldReturnStatus200_whenRoleAdminOrUser(int id, String tagName) {
        RestAssuredMockMvc
                .given()
                .when().get("/tags/" + id)
                .then()
                .status(HttpStatus.OK)
                .body("id", Matchers.is(id))
                .body("name", Matchers.is(tagName));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 1900 })
    @WithMockUser(roles = { "ADMIN", "USER" })
    void findById_shouldReturnStatus404_whenTagWithIdNotFound(int id) {
        RestAssuredMockMvc
                .given()
                .when().get("/tags/" + id)
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("responseCode", Matchers.equalTo("40401"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 7, 1900 })
    void findById_shouldReturnStatus401_whenUserNotUnauthorized(int id) {
        RestAssuredMockMvc
                .given()
                .when().get("/tags/" + id)
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    static Stream<Arguments> validTagProvider() {
        return Stream.of(Arguments.of(new TagDto(null, "new")));
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("validTagProvider")
    @WithMockUser(roles = { "ADMIN" })
    void create_shouldReturnStatus201_whenTagWasCreated(TagDto tagDto) {
        RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON).body(tagDto)
                .when().post("/tags")
                .then()
                .status(HttpStatus.CREATED)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.is("new"));
    }

    @ParameterizedTest
    @MethodSource("validTagProvider")
    @WithMockUser
    void create_shouldReturnStatus403_whenRoleUser(TagDto tagDto) {
        RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON).body(tagDto)
                .when().post("/tags")
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @ParameterizedTest
    @MethodSource("validTagProvider")
    void create_shouldReturnStatus401_whenUserUnauthorized(TagDto tagDto) {
        RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON).body(tagDto)
                .when().post("/tags")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    static Stream<Arguments> invalidTagProvider() {
        return Stream.of(
                Arguments.of(new TagDto(2L, null)),
                Arguments.of(new TagDto(null, null)),
                Arguments.of(new TagDto(null, "")),
                Arguments.of(new TagDto(2L, ""))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTagProvider")
    @WithMockUser(roles = { "ADMIN" })
    void create_shouldReturnStatus400_whenTagBodyInvalid(TagDto tagDto) {
        RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON).body(tagDto)
                .when().post("/tags")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("responseCode", Matchers.equalTo("40001"))
                .body("message", Matchers.notNullValue());
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(ints = { 1, 7 })
    @WithMockUser(roles = { "ADMIN" })
    void delete_shouldReturnStatus204_whenTagWasDeleted(int id) {
        RestAssuredMockMvc
                .given()
                .when().delete("/tags/" + id)
                .then()
                .status(HttpStatus.NO_CONTENT);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 1900 })
    @WithMockUser(roles = { "ADMIN" })
    void delete_shouldReturnStatus404_whenTagWithIdNotFound(int id) {
        RestAssuredMockMvc
                .given()
                .when().delete("/tags/" + id)
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("responseCode", Matchers.equalTo("40401"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 7 })
    @WithMockUser
    void delete_shouldReturnStatus403_whenRoleUser(int id) {
        RestAssuredMockMvc
                .given()
                .when().delete("/tags/" + id)
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 7 })
    void delete_shouldReturnStatus401_whenUserUnauthorized(int id) {
        RestAssuredMockMvc
                .given()
                .when().delete("/tags/" + id)
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

}