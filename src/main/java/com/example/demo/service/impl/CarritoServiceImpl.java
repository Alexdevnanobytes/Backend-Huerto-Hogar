package com.example.demo.service.impl;

import com.example.demo.model.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.CarritoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    public Carrito getOrCreateCarritoActivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return carritoRepository.findByUsuarioAndActivoTrue(usuario)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.builder()
                            .usuario(usuario)
                            .activo(true)
                            .build();
                    return carritoRepository.save(nuevo);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Carrito getCarritoActivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return carritoRepository.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no tiene carrito activo"));
    }

    @Override
    public Carrito addItem(Long usuarioId, Long productoId, Integer cantidad) {

        if (cantidad == null || cantidad < 1) {
            cantidad = 1;
        }

        Carrito carrito = getOrCreateCarritoActivo(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        CarritoItem item = carritoItemRepository
                .findByCarritoAndProducto(carrito, producto)
                .orElse(CarritoItem.builder()
                        .carrito(carrito)
                        .producto(producto)
                        .cantidad(0)
                        .precioUnitario(producto.getPrecio()) // ✅ Double correcto
                        .build()
                );

        item.setCantidad(item.getCantidad() + cantidad);

        carritoItemRepository.save(item);

        actualizarTotal(carrito);

        return carritoRepository.findById(carrito.getId())
                .orElseThrow();
    }

    @Override
    public Carrito updateItemCantidad(Long usuarioId, Long itemId, Integer cantidad) {

        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser >= 1");
        }

        Carrito carrito = getCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado"));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del usuario");
        }

        item.setCantidad(cantidad);

        carritoItemRepository.save(item);

        actualizarTotal(carrito);

        return carritoRepository.findById(carrito.getId())
                .orElseThrow();
    }

    @Override
    public Carrito removeItem(Long usuarioId, Long itemId) {

        Carrito carrito = getCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado"));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del usuario");
        }

        carritoItemRepository.delete(item);

        actualizarTotal(carrito);

        return carritoRepository.findById(carrito.getId())
                .orElseThrow();
    }

    @Override
    public void clearCarrito(Long usuarioId) {
        Carrito carrito = getCarritoActivo(usuarioId);
        carrito.getItems().clear();
        actualizarTotal(carrito);
        carritoRepository.save(carrito);
    }

    // ✅ MÉTODO CENTRAL DE CÁLCULO
    private void actualizarTotal(Carrito carrito) {

        double total = 0;

        for (CarritoItem item : carrito.getItems()) {
            double subtotal = item.getPrecioUnitario() * item.getCantidad();
            total += subtotal;
        }

        carrito.setTotal(total);
        carritoRepository.save(carrito);
    }
}
