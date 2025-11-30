package com.example.demo.repository;

import com.example.demo.model.entity.CarritoItem;
import com.example.demo.model.entity.Carrito;
import com.example.demo.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByCarrito(Carrito carrito);

    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
}
