package com.spring.blog.authentication.infrastructure;

import com.spring.blog.authentication.domain.JwtTokenProvider;
import com.spring.blog.exception.authentication.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final String secretKey;
    private final long expirationTimeInMilliSeconds;

    public JwtTokenProviderImpl(
        @Value("${security.jwt.token.secret-key}") String secretKey,
        @Value("${security.jwt.token.expire-length}") long expirationTimeInMilliSeconds
    ) {
        this.secretKey = secretKey;
        this.expirationTimeInMilliSeconds = expirationTimeInMilliSeconds;
    }

    @Override
    public String createToken(String payload) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expirationTimeInMilliSeconds);
        return Jwts.builder()
            .claim("userName", payload)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            return !claims.getBody()
                .getExpiration()
                .before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getPayloadByKey(String token, String key) {
        try {
            return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get(key, String.class);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }
}
