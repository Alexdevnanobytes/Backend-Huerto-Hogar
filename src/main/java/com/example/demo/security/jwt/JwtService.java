package com.example.demo.security.jwt;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "una_clave_secreta_larga_y_segura_de_al_menos_32_bytes";
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 hora

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 🔹 Generar nuevo JWT
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // 🔹 Extraer username del token
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            System.out.println("❌ Error extrayendo username: " + e.getMessage());
            return null;
        }
    }

    // 🔥 MÉTODO CORRECTO: Validar token con UserDetails
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {

            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            Date expiration = claims.getExpiration();

            boolean notExpired = expiration.after(new Date());
            boolean sameUser = username.equals(userDetails.getUsername());

            System.out.println("🔍 Usuario en token: " + username);
            System.out.println("🔍 Usuario en BD: " + userDetails.getUsername());
            System.out.println("🔍 Token expira: " + expiration);
            System.out.println("🔍 Expirado? " + !notExpired);

            return notExpired && sameUser;

        } catch (JwtException e) {
            System.out.println("❌ Token inválido: " + e.getMessage());
            return false;
        }
    }
}
