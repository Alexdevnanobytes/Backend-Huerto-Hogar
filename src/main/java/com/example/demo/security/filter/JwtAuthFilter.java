package com.example.demo.security.filter;

import com.example.demo.security.jwt.JwtService;
import com.example.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🟢 RUTAS QUE REALMENTE NO NECESITAN JWT
        // (login/register, swagger, h2). OJO: YA NO SALTAMOS /api/v1/usuarios
        if (path.startsWith("/auth")
                || path.startsWith("/h2-console")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 🟡 Lógica normal JWT
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No hay token → se deja pasar como anónimo
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username = jwtService.extractUsername(token);

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            var userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
