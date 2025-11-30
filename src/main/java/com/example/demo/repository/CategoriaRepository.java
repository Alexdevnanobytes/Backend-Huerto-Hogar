package com.example.demo.repository;

import com.example.demo.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Si más adelante quieres buscar por nombre, puedes agregar:
    // Optional<Categoria> findByNombre(String nombre);
}
