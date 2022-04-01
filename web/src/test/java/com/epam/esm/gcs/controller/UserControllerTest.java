package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.model.AccountModel;
import com.epam.esm.gcs.model.AccountRoleModel;
import com.epam.esm.gcs.model.CustomUserDetailsImpl;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Set;
import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    static Stream<Arguments> paginationProvider() {
        return Stream.of(
                Arguments.of(0, 10, 3),
                Arguments.of(2, 10, null),
                Arguments.of(0, 3, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("paginationProvider")
    @WithMockUser(roles = "ADMIN")
    void findAll_shouldReturnStatus200_whenRoleAdmin(Integer page, Integer size, Integer count) {
        RestAssuredMockMvc
                .given()
                .when().get("/users?page=" + page + "&size=" + size)
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.userDtoList.size()", Matchers.is(count));
    }

    @Test
    @WithMockUser
    void findAll_shouldReturnStatus403_whenRoleUser() {
        RestAssuredMockMvc
                .given()
                .when().get("/users")
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void findAll_shouldReturnStatus401_whenUserUnauthorized() {
        RestAssuredMockMvc
                .given()
                .when().get("/users")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest
    @CsvSource({ "2,example2@gmail.com", "3,example3@gmail.com", "4,example4@gmail.com" })
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturnStatus200_whenRoleAdmin(int id, String email) {
        RestAssuredMockMvc
                .given()
                .when().get("/users/" + id)
                .then()
                .status(HttpStatus.OK)
                .body("email", Matchers.is(email));
    }

    @Test
    void findById_shouldReturnStatus200_whenRoleUserAndOwnsInfo() {
        final CustomUserDetailsImpl principal = retrievePrincipal(2L, "example2@gmail.com");
        RestAssuredMockMvc
                .given().auth().principal(principal)
                .when().get("/users/" + 2)
                .then()
                .status(HttpStatus.OK)
                .body("email", Matchers.is("example2@gmail.com"));
    }

    @Test
    void findById_shouldReturnStatus403_whenRoleUserAndNotOwnsInfo() {
        final CustomUserDetailsImpl principal = retrievePrincipal(3L, "example3@gmail.com");
        RestAssuredMockMvc
                .given().auth().principal(principal)
                .when().get("/users/" + 2)
                .then()
                .status(HttpStatus.FORBIDDEN)
                .body("responseCode", Matchers.is("40300"))
                .body("message", Matchers.notNullValue());
    }

    @Transactional
    @ParameterizedTest
    @CsvSource({ "2,4", "2,2", "3,1", "4,3" })
    @WithMockUser(roles = "ADMIN")
    void makePurchase_shouldReturn201_whenOrderSuccessful(int userId, int certificateId) {
        RestAssuredMockMvc
                .given()
                .when().post("/users/" + userId + "/certificates/" + certificateId)
                .then()
                .status(HttpStatus.CREATED)
                .body("id", Matchers.notNullValue())
                .body("cost", Matchers.notNullValue())
                .body("purchaseDate", Matchers.notNullValue())
                .body("certificate", Matchers.notNullValue());
    }

    @ParameterizedTest
    @CsvSource({ "10,10", "-1,2", "2,10" })
    @WithMockUser(roles = "ADMIN")
    void makePurchase_shouldReturn404_whenEntityNotFound(int userId, int certificateId) {
        RestAssuredMockMvc
                .given()
                .when().post("/users/" + userId + "/certificates/" + certificateId)
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("responseCode", Matchers.matchesPattern("^4040[23]$"))
                .body("message", Matchers.notNullValue());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void makePurchase_shouldReturn400_whenNotEnoughMoney() {
        RestAssuredMockMvc
                .given()
                .when().post("/users/4/certificates/4")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("responseCode", Matchers.is("40004"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @CsvSource({ "2,4", "2,2", "4,1", "4,4" })
    void makePurchase_shouldReturn403_whenUserNotOwnsAccount(int userId, int certificateId) {
        final CustomUserDetailsImpl principal = retrievePrincipal(3L, "example3@gmail.com");
        RestAssuredMockMvc
                .given().auth().principal(principal)
                .when().post("/users/" + userId + "/certificates/" + certificateId)
                .then()
                .status(HttpStatus.FORBIDDEN)
                .body("responseCode", Matchers.is("40300"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @CsvSource({ "2,4", "2,2", "4,1", "4,4" })
    void makePurchase_shouldReturn401_whenUserUnauthorized(int userId, int certificateId) {
        RestAssuredMockMvc
                .given()
                .when().post("/users/" + userId + "/certificates/" + certificateId)
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest
    @CsvSource({ "2,3", "3,4", "4,2" })
    @WithMockUser(roles = "ADMIN")
    void findUserOrders_shouldReturnStatus200_whenRoleAdmin(int userId, int count) {
        RestAssuredMockMvc
                .given()
                .when().get("/users/" + userId + "/orders")
                .then()
                .status(HttpStatus.OK)
                .body("_embedded.orderDtoList.size()", Matchers.is(count));
    }

    @ParameterizedTest
    @ValueSource(ints = { 3, 4 })
    void findUserOrders_shouldReturnStatus403_whenRoleUserAndNotOwnsOrders(int userId) {
        final CustomUserDetailsImpl principal = retrievePrincipal(2L, "example2@gmail.com");
        RestAssuredMockMvc
                .given().auth().principal(principal)
                .when().get("/users/" + userId + "/orders")
                .then()
                .status(HttpStatus.FORBIDDEN)
                .body("responseCode", Matchers.is("40300"))
                .body("message", Matchers.notNullValue());
    }

    @Test
    void findUserOrders_shouldReturnStatus401_whenUserUnauthorized() {
        RestAssuredMockMvc
                .given()
                .when().get("/users/3/orders")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest
    @CsvSource({ "2,1", "3,5", "4,9" })
    @WithMockUser(roles = "ADMIN")
    void getTruncatedOrderInfo_shouldReturn201_whenRoleAdmin(int userId, int orderId) {
        RestAssuredMockMvc
                .given()
                .when().get("/users/" + userId + "/orders/" + orderId)
                .then()
                .status(HttpStatus.OK)
                .body("id", Matchers.notNullValue())
                .body("cost", Matchers.notNullValue())
                .body("purchaseDate", Matchers.notNullValue());
    }

    @ParameterizedTest
    @CsvSource({ "10,1", "3,10" })
    @WithMockUser(roles = "ADMIN")
    void getTruncatedOrderInfo_shouldReturn404_whenEntityNotFound(int userId, int orderId) {
        RestAssuredMockMvc
                .given()
                .when().get("/users/" + userId + "/orders/" + orderId)
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("responseCode", Matchers.matchesPattern("^4040[34]$"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @CsvSource({ "2,1", "2,2", "4,1" })
    void getTruncatedOrderInfo_shouldReturn403_whenUserNotOwnsAccount(int userId, int orderId) {
        final CustomUserDetailsImpl principal = retrievePrincipal(3L, "example3@gmail.com");
        RestAssuredMockMvc
                .given().auth().principal(principal)
                .when().get("/users/" + userId + "/orders/" + orderId)
                .then()
                .status(HttpStatus.FORBIDDEN)
                .body("responseCode", Matchers.is("40300"))
                .body("message", Matchers.notNullValue());
    }

    @ParameterizedTest
    @CsvSource({ "2,1", "2,2", "4,1" })
    void getTruncatedOrderInfo_shouldReturn401_whenUserUnauthorized(int userId, int orderId) {
        RestAssuredMockMvc
                .given()
                .when().get("/users/" + userId + "/orders/" + orderId)
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    private CustomUserDetailsImpl retrievePrincipal(long id, String email) {
        return new CustomUserDetailsImpl(
                new AccountModel(
                        id, email, "hashed", true,
                        Set.of(new AccountRoleModel(2L, "ROLE_USER"))
                )
        );
    }

}