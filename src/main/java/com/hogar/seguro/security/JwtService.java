package com.hogar.seguro.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;//10 hours (ms)

    public JwtService(@Value("${jwt.secret.key:}") String yamlKey) {

        if (yamlKey == null || yamlKey.isEmpty() || yamlKey.contains("${")) {
            this.secretKey = System.getenv("JWT_SECRET_KEY");
        } else {
            this.secretKey = yamlKey;
        }

        if (this.secretKey == null || this.secretKey.isEmpty()) {
            throw new IllegalArgumentException("Error crítico: JWT_SECRET_KEY no encontrada en el entorno");
        }
    }


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities());

        return Jwts.builder()

                //--PAYLOAD:--
                .claims(extraClaims)//extraClaim role
                .subject(userDetails.getUsername())//sub claim
                .issuedAt(new Date(System.currentTimeMillis()))//iat claim
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))//exp claim

                //--SIGNATURE:---
                .signWith(getSigningKey())

                .compact();
    }



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);

    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);// R apply(T t)
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}






