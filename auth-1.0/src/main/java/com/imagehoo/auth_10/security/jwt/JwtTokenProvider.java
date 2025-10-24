package com.imagehoo.auth_10.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = "hello9uaunejau7^$fghhye^%5546*&gybgb54521*&b";
    private final String ISSUER = "ISSUER";
    private final long EXPIRES_IN = 1000 * 60 * 2;
    private final long REFRESH_INTERVAL = 1000 * 60 * 60 * 24 * 15;


    public String token(String subject) {

        String secretKey = SECRET_KEY;

        return Jwts.builder()
                .subject(subject)
                .claim("roles", "user")
                .signWith(Keys.builder(Keys.hmacShaKeyFor(secretKey.getBytes())).build(), Jwts.SIG.HS256)
                .issuedAt(new Date())
                .issuer(ISSUER)
                .expiration(new Date(System.currentTimeMillis() + EXPIRES_IN))
                .compact();
    }

    public String token(String subject, String role) {

        SecretKey secretKey = getSigningKey();

        return Jwts.builder()
                .subject(subject)
                .claim("roles", role)
                .signWith(secretKey, Jwts.SIG.HS256)
                .issuedAt(new Date())
                .issuer(ISSUER)
                .expiration(new Date(System.currentTimeMillis() + REFRESH_INTERVAL))
                .compact();
    }

    public boolean validateToken(String token) {
        SecretKey secretKey = getSigningKey();
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubject(String token) {

        SecretKey secretKey = getSigningKey();

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload()
                .getSubject();
    }

    public boolean isExpired(String token) {
        return Jwts.parser().build().isSigned(token) && Jwts.parser().build().parseClaimsJws(token).getPayload().getExpiration().before(new Date());
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
}
