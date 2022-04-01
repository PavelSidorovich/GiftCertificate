package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

@ActiveProfiles({ "test" })
@SpringBootTest
@AutoConfigureMockMvc
class GiftCertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    static Stream<Arguments> validCertificateProvider() {
        final CertificateDto certificate = CertificateDto
                .builder()
                .name("new certificate")
                .description("none")
                .price(BigDecimal.TEN)
                .duration(15)
                .tags(Set.of(
                        new TagDto(null, "family"),
                        new TagDto(null, "to create")))
                .build();
        return Stream.of(Arguments.of(certificate));
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("validCertificateProvider")
    @WithMockUser(roles = { "ADMIN" })
    void create_shouldReturnStatus201_whenCertificateWasCreated(CertificateDto certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().post("/certificates")
                .then()
                .status(HttpStatus.CREATED)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.is("new certificate"))
                .body("description", Matchers.is("none"))
                .body("price", Matchers.is(10))
                .body("duration", Matchers.is(15))
                .body("createDate", Matchers.notNullValue())
                .body("lastUpdateDate", Matchers.notNullValue())
                .body("tags.size()", Matchers.is(2))
                .body("tags[0]", Matchers.hasEntry("name", "to create"))
                .body("tags[1]", Matchers.hasEntry("name", "family"));
    }

    @ParameterizedTest
    @MethodSource("validCertificateProvider")
    @WithMockUser
    void create_shouldReturnStatus403_whenRoleUser(CertificateDto certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().post("/certificates")
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @ParameterizedTest
    @MethodSource("validCertificateProvider")
    void create_shouldReturnStatus403_whenUserUnauthorized(CertificateDto certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().post("/certificates")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    static Stream<Arguments> invalidCertificatesProvider() {
        final CertificateDto certificate1 = CertificateDto
                .builder()
                .name("new certificate")
                .duration(15)
                .tags(Set.of(
                        new TagDto(null, "family"),
                        new TagDto(null, "to create")))
                .build();
        final CertificateDto certificate2 = CertificateDto
                .builder()
                .description("none")
                .tags(Set.of(
                        new TagDto(null, "family"),
                        new TagDto(null, "to create")))
                .build();
        final CertificateDto certificate3 = CertificateDto.builder().build();
        return Stream.of(
                Arguments.of(certificate1),
                Arguments.of(certificate2),
                Arguments.of(certificate3)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCertificatesProvider")
    @WithMockUser(roles = { "ADMIN" })
    void create_shouldReturnStatus400_whenCertificateBodyIsInvalid(CertificateDto certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().post("/certificates")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("responseCode", Matchers.equalTo("40002"))
                .body("message", Matchers.notNullValue());
    }

    static Stream<Arguments> filterUriProvider() {
        return Stream.of(
                Arguments.of("/certificates?tagName=family", 2),
                Arguments.of("/certificates?tagName=family&certName=new", null),
                Arguments.of("/certificates?description=wonderful", 1),
                Arguments.of("/certificates?sortByCreateDate=ASC&sortByName=DESC", 5),
                Arguments.of("/certificates?page=0&size=2", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("filterUriProvider")
    @WithMockUser(roles = { "USER", "ADMIN" })
    void findByFilter_shouldReturnStatus200_always(String path, Integer count) {
        RestAssuredMockMvc
                .given()
                .when().get(path)
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.certificateDtoList.size()", Matchers.equalTo(count));
    }

    @ParameterizedTest
    @MethodSource("filterUriProvider")
    void findByFilter_shouldReturnStatus200_whenUnauthorized(String path, Integer count) {
        RestAssuredMockMvc
                .given()
                .when().get(path)
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.certificateDtoList.size()", Matchers.equalTo(count));
    }

    static Stream<Arguments> filterByTagsUriProvider() {
        return Stream.of(
                Arguments.of("/certificates?tag=family&tag=health", 4),
                Arguments.of("/certificates?tag=toy", null),
                Arguments.of("/certificates?tag=sport&tag=gift", 3),
                Arguments.of("/certificates?tag=sport&tag=tech&tag=learning", 3)
        );
    }

    @ParameterizedTest
    @MethodSource("filterByTagsUriProvider")
    @WithMockUser(roles = { "USER", "ADMIN" })
    void findByTags_shouldReturnStatus200_always(String path, Integer count) {
        RestAssuredMockMvc
                .given()
                .when().get(path)
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.certificateDtoList.size()", Matchers.equalTo(count));
    }

    @ParameterizedTest
    @MethodSource("filterByTagsUriProvider")
    void findByTags_shouldReturnStatus200_whenUserUnauthorized(String path, Integer count) {
        RestAssuredMockMvc
                .given()
                .when().get(path)
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.certificateDtoList.size()", Matchers.equalTo(count));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 5 })
    @WithMockUser(roles = { "USER", "ADMIN" })
    void findById_shouldReturnStatus200_whenCertificateFoundById(int id) {
        RestAssuredMockMvc
                .given()
                .when().get("/certificates/" + id)
                .then()
                .status(HttpStatus.OK)
                .body("id", Matchers.is(id))
                .body("name", Matchers.notNullValue())
                .body("description", Matchers.notNullValue())
                .body("price", Matchers.notNullValue())
                .body("duration", Matchers.notNullValue())
                .body("createDate", Matchers.notNullValue())
                .body("lastUpdateDate", Matchers.notNullValue())
                .body("tags", Matchers.notNullValue());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 5 })
    @WithAnonymousUser
    void findById_shouldReturnStatus200_whenUserUnauthorized(int id) {
        RestAssuredMockMvc
                .given()
                .when().get("/certificates/" + id)
                .then()
                .status(HttpStatus.OK)
                .body("id", Matchers.is(id))
                .body("name", Matchers.notNullValue())
                .body("description", Matchers.notNullValue())
                .body("price", Matchers.notNullValue())
                .body("duration", Matchers.notNullValue())
                .body("createDate", Matchers.notNullValue())
                .body("lastUpdateDate", Matchers.notNullValue())
                .body("tags", Matchers.notNullValue());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 1900 })
    @WithMockUser(roles = { "USER", "ADMIN" })
    void findById_shouldReturnStatus404_whenCertificateNotFound(int id) {
        RestAssuredMockMvc
                .given()
                .when().get("/certificates/" + id)
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("responseCode", Matchers.is("40402"))
                .body("message", Matchers.notNullValue());
    }

    static Stream<Arguments> validUpdatedCertificateProvider() {
        final CertificateDto certificate = CertificateDto
                .builder()
                .description("sportswear discount")
                .price(BigDecimal.ONE)
                .duration(9)
                .tags(Set.of(
                        new TagDto(null, "family"),
                        new TagDto(null, "body")))
                .build();
        return Stream.of(Arguments.of(certificate));
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("validUpdatedCertificateProvider")
    @WithMockUser(roles = { "ADMIN" })
    void update_shouldReturnStatus200_whenCertificateWasUpdated(CertificateDto certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().patch("/certificates/" + 1L)
                .then()
                .status(HttpStatus.OK)
                .body("id", Matchers.is(1))
                .body("name", Matchers.is("Sport wear certificate"))
                .body("description", Matchers.is("sportswear discount"))
                .body("price", Matchers.is(1))
                .body("duration", Matchers.is(9))
                .body("createDate", Matchers.notNullValue())
                .body("lastUpdateDate", Matchers.notNullValue())
                .body("tags.size()", Matchers.is(2));
    }

    static Stream<Arguments> invalidCertificateToUpdateProvider() {
        final CertificateDto certificate1 = CertificateDto
                .builder()
                .id(1L)
                .name("name")
                .build();
        final CertificateDto certificate2 = CertificateDto
                .builder()
                .name("name")
                .build();
        return Stream.of(
                Arguments.of(certificate1),
                Arguments.of(certificate2),
                Arguments.of("{}")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCertificateToUpdateProvider")
    @WithMockUser(roles = { "ADMIN" })
    void update_shouldReturnStatus400_whenCertificateBodyIsInvalid(Object certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().patch("/certificates/" + 1L)
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("responseCode", Matchers.is("40002"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @MethodSource("validUpdatedCertificateProvider")
    void update_shouldReturnStatus401_whenUserUnauthorized(CertificateDto certificate) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(certificate)
                .when().patch("/certificates/" + 1L)
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 5 })
    @WithMockUser(roles = { "ADMIN" })
    void delete_shouldReturn204_whenCertificateWasDeleted(int id) {
        RestAssuredMockMvc
                .given()
                .when().delete("/certificates/" + id)
                .then()
                .status(HttpStatus.NO_CONTENT);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 1900 })
    @WithMockUser(roles = { "ADMIN" })
    void delete_shouldReturn404_whenCertificateToDeleteNotFound(int id) {
        RestAssuredMockMvc
                .given()
                .when().delete("/certificates/" + id)
                .then()
                .status(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockUser
    void delete_shouldReturn403_whenRoleUser() {
        RestAssuredMockMvc
                .given()
                .when().delete("/certificates/1")
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void delete_shouldReturn401_whenUserUnauthorized() {
        RestAssuredMockMvc
                .given()
                .when().delete("/certificates/1")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

}