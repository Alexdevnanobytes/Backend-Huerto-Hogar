package com.example.demo.service;

import com.example.demo.model.entity.Carrito;

public interface CarritoService {

    Carrito getOrCreateCarritoActivo(Long usuarioId);

    Carrito getCarritoActivo(Long usuarioId);

    Carrito addItem(Long usuarioId, Long productoId, Integer cantidad);

    Carrito updateItemCantidad(Long usuarioId, Long itemId, Integer cantidad);

    Carrito removeItem(Long usuarioId, Long itemId);

    void clearCarrito(Long usuarioId);
}
