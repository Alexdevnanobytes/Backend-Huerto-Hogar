package com.example.demo.controller.v1;

import com.example.demo.model.dto.UsuarioCreateDto;
import com.example.demo.model.entity.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // ================================
    //   ENDPOINT PARA OBTENER EL USUARIO LOGUEADO
    // ================================
    @GetMapping("/me")
    public ResponseEntity<Usuario> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();

        return usuarioService
                .findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody UsuarioCreateDto dto) {
        Usuario creado = usuarioService.register(dto);
        return ResponseEntity.created(
                URI.create("/api/v1/usuarios/" + creado.getId())
        ).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(
            @PathVariable Long id,
            @RequestBody Usuario usuario
    ) {
        return usuarioService.findById(id)
                .map(actual -> {
                    actual.setUsername(usuario.getUsername());
                    actual.setEmail(usuario.getEmail());
                    actual.setEnabled(usuario.isEnabled());
                    actual.setRoles(usuario.getRoles());

                    Usuario guardado = usuarioService.save(actual);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (usuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
