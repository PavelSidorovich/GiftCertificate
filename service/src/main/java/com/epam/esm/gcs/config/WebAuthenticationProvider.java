package com.epam.esm.gcs.config;

import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class WebAuthenticationProvider implements AuthenticationProvider {

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
        } catch (EntityNotFoundException ignored) {
        }
        throw new BadCredentialsException("invalid email or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
