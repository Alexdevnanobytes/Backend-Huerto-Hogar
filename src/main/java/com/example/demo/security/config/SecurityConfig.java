package com.example.demo.security.config;

import com.example.demo.security.filter.JwtAuthFilter;
import com.example.demo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Auth (login / register)
                        .requestMatchers("/auth/**").permitAll()

                        // 🔓 Catálogo público (solo lectura)
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/productos/**",
                                "/api/v1/categorias/**"
                        ).permitAll()

                        // 👤 Usuario actual (/me) → cualquier autenticado
                        .requestMatchers("/api/v1/usuarios/me").authenticated()

                        // 👑 Administración de usuarios → solo ADMIN
                        .requestMatchers("/api/v1/usuarios/**").hasAuthority("ADMIN")

                        // 👑 Roles → solo ADMIN
                        .requestMatchers("/api/v1/roles/**").hasAuthority("ADMIN")

                        // 🧑‍💼 Productos (POST/PUT/DELETE) → ADMIN o SELL
                        .requestMatchers(HttpMethod.POST,   "/api/v1/productos/**").hasAnyAuthority("ADMIN", "SELL")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/productos/**").hasAnyAuthority("ADMIN", "SELL")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasAnyAuthority("ADMIN", "SELL")

                        // 🛒 Carrito → cualquier autenticado
                        .requestMatchers("/api/v1/carritos/**").authenticated()

                        // Swagger / H2
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 🔒 Todo lo demás autenticado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
