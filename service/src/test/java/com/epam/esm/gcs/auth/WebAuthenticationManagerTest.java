package com.epam.esm.gcs.auth;

import com.epam.esm.gcs.auth.impl.WebAuthenticationManager;
import com.epam.esm.gcs.exception.BadCredentialsException;
import com.epam.esm.gcs.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebAuthenticationManagerTest {

    private final WebAuthenticationManager webAuthProvider;

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public WebAuthenticationManagerTest(@Mock PasswordEncoder passwordEncoder,
                                        @Mock UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.webAuthProvider =
                new WebAuthenticationManager(passwordEncoder, userService);
    }

    @Test
    void authenticate_shouldPass_whenUserLoginAndPasswordAreValid() {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("test@gmail.com", "pass", Collections.emptySet());
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("$2hashed");
        when(userService.loadUserByUsername("test@gmail.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("pass", "$2hashed")).thenReturn(true);

        Authentication actual = webAuthProvider.authenticate(token);

        assertTrue(actual.isAuthenticated());
        assertEquals("test@gmail.com", actual.getPrincipal());
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenUserLoginAndPasswordAreInvalid() {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("test@gmail.com", "pass", Collections.emptySet());
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("$2hashed");
        when(userService.loadUserByUsername("test@gmail.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("pass", "$2hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> webAuthProvider.authenticate(token));
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenUserNotFound() {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("test@gmail.com", "pass", Collections.emptySet());
        when(userService.loadUserByUsername("test@gmail.com")).thenThrow(UsernameNotFoundException.class);

        assertThrows(BadCredentialsException.class, () -> webAuthProvider.authenticate(token));
    }

}