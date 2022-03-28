package com.epam.esm.gcs.auth;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenUtil {

    String generateToken(UserDetails userDetails);

    boolean validateToken(String token, UserDetails userDetails);

    String retrievePrincipal(String token);

}
