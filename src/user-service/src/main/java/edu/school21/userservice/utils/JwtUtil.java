package edu.school21.userservice.utils;

import edu.school21.userservice.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    private final RsaKeyProvider rsaKeyProvider;

    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(rsaKeyProvider.privateKey())
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return parseAndValidateToken(token)
                .getSubject();
    }

    public Claims parseAndValidateToken(String token) {
        token = extract(token);
        if (Objects.isNull(token)) {
            throw new InvalidJwtTokenException("JWT токен отсутствует или не начинается с 'Bearer '");
        }
        try {
            return Jwts.parser()
                    .verifyWith(rsaKeyProvider.publicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException e) {
            throw new InvalidJwtTokenException("Недопустимая подпись JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new InvalidJwtTokenException("Недопустимый токен JWT: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtTokenException("Срок действия токена JWT истек: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new InvalidJwtTokenException("Токен JWT не поддерживается: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException("Неверный JWT токен: " + e.getMessage());
        }
    }

    private @Nullable String extract(String token) {
        if (Objects.isNull(token)) {
            return null;
        }
        if (!token.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            return null;
        }
        token = token.substring("bearer ".length());
        return token;
    }
}
