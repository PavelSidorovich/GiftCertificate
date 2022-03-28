package com.epam.esm.gcs.filter;

import com.epam.esm.gcs.model.CustomUserDetails;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.auth.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        String email = null;

        if (tokenHeader != null && tokenHeader.startsWith(BEARER_PREFIX)) {
            token = tokenHeader.substring(7);
            try {
                email = jwtTokenService.retrievePrincipal(token);
            } catch (IllegalArgumentException ex) {
                log.error("Unable to get JWT token");
            } catch (ExpiredJwtException ex) {
                log.error("JWT token has expired");
            }
        } else {
            log.warn("JWT token header doesn't begin with 'Bearer '");
        }
        authenticateIfNeeded(request, token, email);
        filterChain.doFilter(request, response);
    }

    private void authenticateIfNeeded(HttpServletRequest request, String token, String email) {
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(email);
            if (jwtTokenService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

}
