package com.example.demo.repository;

import com.example.demo.model.entity.Carrito;
import com.example.demo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioAndActivoTrue(Usuario usuario);
}
