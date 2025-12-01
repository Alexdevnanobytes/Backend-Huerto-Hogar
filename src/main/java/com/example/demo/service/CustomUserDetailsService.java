package com.example.demo.service;

import com.example.demo.model.entity.Rol;
import com.example.demo.model.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository userRepository;

    public CustomUserDetailsService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 👉 Convertimos los roles de la BD en authorities de Spring
        List<GrantedAuthority> authorities = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)                       // "ADMIN", "USER", "SELL"
                .map(SimpleGrantedAuthority::new)          // new SimpleGrantedAuthority("ADMIN")
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
