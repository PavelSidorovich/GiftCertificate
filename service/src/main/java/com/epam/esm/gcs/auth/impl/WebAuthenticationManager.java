package com.epam.esm.gcs.auth.impl;

import com.epam.esm.gcs.exception.BadCredentialsException;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class WebAuthenticationProvider implements AuthenticationManager {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            final UsernamePasswordAuthenticationToken upAuth = (UsernamePasswordAuthenticationToken) authentication;
            final String email = (String) authentication.getPrincipal();
            final String password = (String) upAuth.getCredentials();
            final String storedPassword = userService.loadUserByUsername(email).getPassword();

            if (passwordEncoder.matches(password, storedPassword)) {
                final Object principal = authentication.getPrincipal();
                final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                        principal, authentication.getCredentials(),
                        Collections.emptyList()
                );
                result.setDetails(authentication.getDetails());
                return result;
            }
        } catch (UsernameNotFoundException ignored) {
        }
        throw new BadCredentialsException();
    }

}
