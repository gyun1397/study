package com.domain.util;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenUtil {
    public static final String SECRET_KEY_FOR_JWT_TOKEN       = "templateKey";
    public final static Long   TOKEN_VALIDATION_SECOND        = 1000L * 60 * 60 * 3;

    public static String makeToken(Map<String, Object> claims) {
        return makeJWT(claims, TOKEN_VALIDATION_SECOND);
    }

    public static String makeJWT(Map<String, Object> claims, Long expiresAt) {
        return Jwts.builder()
                .setHeader(createHeader())
                .setExpiration(new Date(System.currentTimeMillis() + expiresAt))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, createSigningKey()).compact();
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    private static Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY_FOR_JWT_TOKEN);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public static Optional<String> extractJWT(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null) {
            return Optional.empty();
        }
        return Optional.of(bearerToken.replace("Bearer ", ""));
    }

    public static Optional<String> verifyJWT(String token) {
        try {
            Claims claims = getClaims(token);
            log.debug("expireTime :" + claims.getExpiration());
            log.debug("role :" + claims.get("role"));
            return Optional.of(claims.get("username", String.class));
        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");
            return Optional.empty();
        } catch (JwtException exception) {
            log.error("Token Tampered");
            return Optional.empty();
        } catch (NullPointerException exception) {
            log.error("Token is null");
            return Optional.empty();
        }
    }

    public static String getClaim(String token, String claim) {
        return getClaims(token).get(claim, String.class);
    }

    public static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY_FOR_JWT_TOKEN))
                .parseClaimsJws(token)
                .getBody();
    }
    
    public static Date getExpireDate(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY_FOR_JWT_TOKEN))
                .parseClaimsJws(token)
                .getBody().getExpiration();
    }
    
    public static Collection<? extends GrantedAuthority> getAuthorities(String token) {
        try {
            List<String> roles = (List<String>) getClaims(token).get("role", Collection.class);
            return roles == null || roles.size() == 0
                    ? Collections.emptyList()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
