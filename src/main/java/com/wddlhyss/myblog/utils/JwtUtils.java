package com.wddlhyss.myblog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey secretKey;

    private final long expireSeconds;

    private final String issuer;

    public JwtUtils(
            @Value("${jwt.secret}")
            String secret,

            @Value("${jwt.expire-seconds:604800}")
            long expireSeconds,

            @Value("${jwt.issuer:chtholly-schedule}")
            String issuer
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT密钥不能为空"
            );
        }

        if (expireSeconds <= 0) {
            throw new IllegalArgumentException(
                    "JWT有效时间必须大于0"
            );
        }

        byte[] keyBytes =
                Decoders.BASE64.decode(secret);

        this.secretKey =
                Keys.hmacShaKeyFor(keyBytes);

        this.expireSeconds =
                expireSeconds;

        this.issuer =
                issuer;
    }

    public String generateToken(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "生成JWT时用户ID不能为空"
            );
        }

        Date now = new Date();

        Date expiration =
                new Date(
                        now.getTime() +
                                expireSeconds * 1000
                );

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(
                        String.valueOf(userId)
                )
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public Long parseUserId(String token) {
        Claims claims =
                parseClaims(token);

        String userIdText =
                claims.getSubject();

        if (
                userIdText == null ||
                        userIdText.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "JWT中不存在用户ID"
            );
        }

        try {
            return Long.valueOf(
                    userIdText
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "JWT中的用户ID格式错误",
                    exception
            );
        }
    }

    public Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT不能为空"
            );
        }

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (
                JwtException |
                IllegalArgumentException exception
        ) {
            return false;
        }
    }

    public String extractToken(
            String authorization
    ) {
        if (
                authorization == null ||
                        !authorization.startsWith("Bearer ")
        ) {
            throw new IllegalArgumentException(
                    "Authorization请求头格式错误"
            );
        }

        String token =
                authorization.substring(7);

        if (token.isBlank()) {
            throw new IllegalArgumentException(
                    "Bearer Token不能为空"
            );
        }

        return token;
    }
}