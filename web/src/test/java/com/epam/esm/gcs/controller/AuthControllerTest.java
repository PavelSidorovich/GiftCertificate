package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.LoginUserDto;
import com.epam.esm.gcs.dto.SignUpUserDto;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final ObjectMapper OBJECT_MAPPER;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    static {
        OBJECT_MAPPER = JsonMapper.builder()
                                  .disable(MapperFeature.USE_ANNOTATIONS)
                                  .build();
    }

    static Stream<Arguments> validLoginContentProvider() throws IOException {
        final LoginUserDto loginContent = new LoginUserDto(
                "example1@gmail.com", "12345678"
        );
        return Stream.of(Arguments.of(OBJECT_MAPPER.writeValueAsString(loginContent)));
    }

    @ParameterizedTest
    @MethodSource("validLoginContentProvider")
    void login_shouldReturnStatus200_whenUserCredentialsAreValid(String loginContent) {
        final String actualToken = RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON).body(loginContent)
                .when()
                .post("/auth/login")
                .then()
                .status(HttpStatus.OK)
                .extract().response().asString();

        assertEquals(148, actualToken.length());
    }

    static Stream<Arguments> invalidLoginContentProvider() throws IOException {
        final LoginUserDto invalidEmailLoginContent = new LoginUserDto(
                "example@gmail.com", "12345678"
        );
        final LoginUserDto invalidPasswordLoginContent = new LoginUserDto(
                "example1@gmail.com", "1234567"
        );
        return Stream.of(
                Arguments.of(OBJECT_MAPPER.writeValueAsString(invalidEmailLoginContent)),
                Arguments.of(OBJECT_MAPPER.writeValueAsString(invalidPasswordLoginContent))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLoginContentProvider")
    void login_shouldReturnStatus401_whenUserCredentialsAreInvalid(String loginContent) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(loginContent)
                .when().post("/auth/login")
                .then()
                .status(HttpStatus.UNAUTHORIZED)
                .body("responseCode", Matchers.equalTo("40100"))
                .body("message", Matchers.notNullValue());
    }

    static Stream<Arguments> validSignUpContentProvider() throws IOException {
        final SignUpUserDto signUpUserDto = SignUpUserDto
                .signUpBuilder()
                .email("signUp@gmail.com")
                .password("12345678")
                .passwordRepeat("12345678")
                .firstName("Name")
                .lastName("Surname")
                .build();
        return Stream.of(Arguments.of(OBJECT_MAPPER.writeValueAsString(signUpUserDto)));
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("validSignUpContentProvider")
    void signUp_shouldReturnStatus200_whenUserInformationIsValid(String signUpContent) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(signUpContent)
                .when().post("/auth/signup")
                .then()
                .status(HttpStatus.OK)
                .body("email", Matchers.equalTo("signUp@gmail.com"))
                .body("password", Matchers.nullValue())
                .body("firstName", Matchers.equalTo("Name"))
                .body("lastName", Matchers.equalTo("Surname"))
                .body("id", Matchers.notNullValue())
                .body("balance", Matchers.equalTo(0))
                .body("enabled", Matchers.equalTo(true));
    }

    static Stream<Arguments> duplicateSignUpContentProvider() throws IOException {
        final SignUpUserDto duplicateSignUpBody = SignUpUserDto
                .signUpBuilder()
                .email("example1@gmail.com")
                .password("12345678")
                .passwordRepeat("12345678")
                .firstName("Name")
                .lastName("Surname")
                .build();
        return Stream.of(Arguments.of(OBJECT_MAPPER.writeValueAsString(duplicateSignUpBody)));
    }

    @ParameterizedTest
    @MethodSource("duplicateSignUpContentProvider")
    void signUp_shouldReturnStatus409_whenEmailAlreadyExists(String signUpContent) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(signUpContent)
                .when().post("/auth/signup")
                .then()
                .status(HttpStatus.CONFLICT)
                .body("responseCode", Matchers.equalTo("40903"))
                .body("message", Matchers.notNullValue());
    }

    static Stream<Arguments> passwordsNotEqualSignUpContentProvider() throws IOException {
        final SignUpUserDto duplicateSignUpBody = SignUpUserDto
                .signUpBuilder()
                .email("example2@gmail.com")
                .password("12345678")
                .passwordRepeat("1234567")
                .firstName("Name")
                .lastName("Surname")
                .build();
        return Stream.of(Arguments.of(OBJECT_MAPPER.writeValueAsString(duplicateSignUpBody)));
    }

    @ParameterizedTest
    @MethodSource("passwordsNotEqualSignUpContentProvider")
    void signUp_shouldReturnStatus400_whenPasswordsAreNotSame(String signUpContent) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(signUpContent)
                .when().post("/auth/signup")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("responseCode", Matchers.equalTo("40000"))
                .body("message", Matchers.notNullValue());
    }

    static Stream<Arguments> invalidSignUpContentProvider() {
        return Stream.of(Arguments.of("{}"));
    }

    @ParameterizedTest
    @MethodSource("invalidSignUpContentProvider")
    void signUp_shouldReturnStatus400_whenUserInformationIsInvalid(String signUpContent) {
        RestAssuredMockMvc
                .given().contentType(ContentType.JSON).body(signUpContent)
                .when().post("/auth/signup")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("responseCode", Matchers.equalTo("40000"))
                .body("message", Matchers.notNullValue());
    }

}