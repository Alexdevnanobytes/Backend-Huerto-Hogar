package com.example.demo.controller.v1;

import com.example.demo.model.dto.UsuarioCreateDto;
import com.example.demo.security.jwt.JwtService;
import com.example.demo.service.UsuarioService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UsuarioService usuarioService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody UsuarioCreateDto dto) {

        usuarioService.register(dto);  // 🔥 IMPORTANTE

        return Map.of("message", "Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        if (auth.isAuthenticated()) {
            String token = jwtService.generateToken(username);
            return Map.of("token", token);
        }

        throw new RuntimeException("Credenciales inválidas");
    }
}
