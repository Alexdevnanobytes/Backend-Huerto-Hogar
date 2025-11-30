package com.example.demo.service;

import com.example.demo.model.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> findAll();

    Optional<Producto> findById(Long id);

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByCategoriaCodigo(String categoriaCodigo);

    Producto save(Producto producto);

    void deleteById(Long id);
}
