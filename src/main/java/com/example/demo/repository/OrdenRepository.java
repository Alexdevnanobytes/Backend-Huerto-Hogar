package com.example.demo.repository;

import com.example.demo.model.entity.Orden;
import com.example.demo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuario(Usuario usuario);
}
