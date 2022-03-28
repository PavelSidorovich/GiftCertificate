package com.epam.esm.gcs.auth.impl;

import com.epam.esm.gcs.config.TestConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

@SpringBootTest(classes = { TestConfig.class })
@EnableAutoConfiguration
class JwtTokenServiceImplTest {

    private final JwtTokenServiceImpl jwtTokenService;

    @Autowired
    public JwtTokenServiceImplTest(JwtTokenServiceImpl jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    static Stream<Arguments> userProvider() {
        return Stream.of(
                arguments(new User(
                        "username", "password",
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                ))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "userProvider")
    void generateToken_shouldReturnJwtToken_always(UserDetails userDetails) {
        final String token = jwtTokenService.generateToken(userDetails);

        assertNotNull(token);
    }

    @ParameterizedTest
    @MethodSource(value = "userProvider")
    void retrievePrincipal_shouldRetrieveThePrincipal_always(UserDetails userDetails) {
        final String token = jwtTokenService.generateToken(userDetails);
        final String expected = userDetails.getUsername();

        final String actual = jwtTokenService.retrievePrincipal(token);

        assertEquals(expected, actual);
    }

    static Stream<Arguments> userDetailsProvider() {
        return Stream.of(
                arguments(new User(
                        "username", "password",
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                ), true),
                arguments(new User(
                        "fail", "password",
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                ), false),
                arguments(new User(
                        "username", "pass",
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                ), true),
                arguments(new User(
                        "username", "pass",
                        List.of(new SimpleGrantedAuthority("USER"))
                ), true)
        );
    }

    @ParameterizedTest
    @MethodSource(value = "userDetailsProvider")
    void validateToken_shouldReturnTrue_whenTokenIsValid(UserDetails userDetails, boolean expected) {
        final UserDetails user = new User(
                "username", "password",
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        final String token = jwtTokenService.generateToken(user);

        final boolean actual = jwtTokenService.validateToken(token, userDetails);

        assertEquals(expected, actual);
    }

}