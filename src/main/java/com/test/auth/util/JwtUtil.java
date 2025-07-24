package com.test.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service

public class JwtUtil {

    Logger log = LogManager.getLogger(JwtUtil.class);

    // 🔐 Secret key in Base64 format loaded from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // ⏱️ Token expiration time in milliseconds (e.g. 86400000 = 24 hours)
    @Value("${jwt.expiration}")
    private long expiration;

    // ⚙️ Decoded HMAC key used for signing and validating tokens
    private SecretKey secretKey;

    // ✅ Initializes the secretKey once after all @Value fields are injected
    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * ✅ Generates a JWT with dynamic claims.
     *
     * @param username        The subject (typically user's email/username)
     * @param extraClaims  Additional claims (e.g., role, userId, isVerified)
     * @return             Signed JWT string
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);

        return Jwts.builder()
                .setClaims(extraClaims)               // Adds dynamic claims like role, userId, etc.
                .setSubject(username)                    // Sets the subject of the token (user identity)
                .setIssuedAt(Date.from(now))          // When the token was created
                .setExpiration(Date.from(expiry))     // When the token expires
                .signWith(secretKey, SignatureAlgorithm.HS256) // Sign with secure key using HMAC SHA-256
                .compact();
    }

    /**
     * ✅ Extract a specific claim from the JWT.
     *
     * @param token            JWT token string
     * @param claimsResolver   Lambda function to extract desired claim from Claims object
     * @return                 The resolved claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * ✅ Extracts the username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ✅ Extracts the 'role' claim from the token.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * ✅ Extracts all claims by parsing the JWT with the signing key.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ Checks if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * ✅ Validates the JWT's structure, signature, and expiration.
     */
    public boolean isValidToken(String token, UserDetails userDetails) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
           log.info("Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.info("Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.info("Invalid signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("Empty or null token: {}", e.getMessage());
        }
        return false;
    }

}
