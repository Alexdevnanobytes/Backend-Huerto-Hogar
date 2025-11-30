package com.example.demo.service;

import com.example.demo.model.entity.Orden;

import java.util.List;

public interface OrdenService {

    Orden crearOrdenDesdeCarrito(Long usuarioId, String direccionEntrega);

    Orden findById(Long id);

    List<Orden> findByUsuario(Long usuarioId);
}
