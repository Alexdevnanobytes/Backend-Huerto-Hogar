package com.example.demo.service;

import com.example.demo.model.dto.UsuarioCreateDto;
import com.example.demo.model.entity.Rol;
import com.example.demo.model.entity.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    // REGISTER
    // ============================================================
    public Usuario register(UsuarioCreateDto dto) {

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener mínimo 4 caracteres");
        }

        if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new IllegalStateException("Rol USER no existe en BD"));

        Usuario nuevo = Usuario.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(true)
                .roles(Set.of(rolUser))
                .build();

        return usuarioRepository.save(nuevo);
    }

    // ============================================================
    // MÉTODO QUE FALTABA
    // ============================================================
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }


    // ============================================================
    // CRUD NORMAL
    // ============================================================
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
