package com.example.demo.service;

import com.example.demo.model.entity.Carrito;

public interface CarritoService {

    /**
     * Devuelve el carrito activo del usuario si existe,
     * o lo crea si no existe.
     */
    Carrito getOrCreateCarritoActivo(Long usuarioId);

    /**
     * Devuelve el carrito activo del usuario.
     * Lanza EntityNotFoundException si no existe.
     */
    Carrito getCarritoActivo(Long usuarioId);

    /**
     * Agrega un producto al carrito del usuario.
     */
    Carrito addItem(Long usuarioId, Long productoId, Integer cantidad);

    /**
     * Actualiza la cantidad de un ítem del carrito.
     */
    Carrito updateItemCantidad(Long usuarioId, Long itemId, Integer cantidad);

    /**
     * Elimina un ítem del carrito.
     */
    Carrito removeItem(Long usuarioId, Long itemId);

    /**
     * Limpia por completo el carrito del usuario.
     */
    void clearCarrito(Long usuarioId);
}
