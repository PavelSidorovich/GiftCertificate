package com.epam.esm.gcs.auth.impl;

import com.epam.esm.gcs.auth.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtTokenUtilImpl implements JwtTokenUtil {

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String secret;

    @Override
    public String generateToken(UserDetails userDetails) {
        final HashMap<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                   .signWith(SIGNATURE_ALGORITHM, secret)
                   .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = retrievePrincipal(token);
        return email.equals(userDetails.getUsername()) && !isExpired(token);
    }

    @Override
    public String retrievePrincipal(String token) {
        return retrieveClaim(token, Claims::getSubject);
    }

    private Date retrieveExpiration(String token) {
        return retrieveClaim(token, Claims::getExpiration);
    }

    private <T> T retrieveClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = retrieveClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims retrieveClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(secret)
                   .parseClaimsJws(token)
                   .getBody();
    }

    private boolean isExpired(String token) {
        final Date expiration = retrieveExpiration(token);
        return expiration.before(new Date());
    }

}
