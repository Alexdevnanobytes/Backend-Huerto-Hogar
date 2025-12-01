package com.example.demo.service.impl;

import com.example.demo.model.entity.Carrito;
import com.example.demo.model.entity.CarritoItem;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.Usuario;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    // =========================================================
    // Obtener o crear carrito activo
    // =========================================================
    @Override
    public Carrito getOrCreateCarritoActivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return carritoRepository.findByUsuarioAndActivoTrue(usuario)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.builder()
                            .usuario(usuario)
                            .activo(true)
                            .total(0.0)
                            .build();
                    return carritoRepository.save(nuevo);
                });
    }

    // =========================================================
    // Obtener carrito activo (lanza excepción si no existe)
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public Carrito getCarritoActivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return carritoRepository.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no tiene carrito activo"));
    }

    // =========================================================
    // Agregar ítem
    // =========================================================
    @Override
    public Carrito addItem(Long usuarioId, Long productoId, Integer cantidad) {

        if (cantidad == null || cantidad < 1) {
            cantidad = 1;
        }

        Carrito carrito = getOrCreateCarritoActivo(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Buscar item existente dentro del carrito
        CarritoItem item = carrito.getItems().stream()
                .filter(ci -> ci.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecio())
                    .build();
            carrito.getItems().add(item);
        } else {
            item.setCantidad(item.getCantidad() + cantidad);
        }

        actualizarTotal(carrito);
        return carritoRepository.save(carrito);
    }

    // =========================================================
    // Actualizar cantidad de ítem
    // =========================================================
    @Override
    public Carrito updateItemCantidad(Long usuarioId, Long itemId, Integer cantidad) {

        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser >= 1");
        }

        Carrito carrito = getCarritoActivo(usuarioId);

        // Buscar el item dentro del carrito
        CarritoItem item = carrito.getItems().stream()
                .filter(ci -> ci.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado en el carrito"));

        item.setCantidad(cantidad);

        actualizarTotal(carrito);
        return carritoRepository.save(carrito);
    }

    // =========================================================
    // Eliminar ítem
    // =========================================================
    @Override
    public Carrito removeItem(Long usuarioId, Long itemId) {

        Carrito carrito = getCarritoActivo(usuarioId);

        // Buscamos el ítem dentro del carrito
        CarritoItem item = carrito.getItems().stream()
                .filter(ci -> ci.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado en el carrito"));

        // Sacamos el ítem de la colección
        carrito.getItems().remove(item);

        // Gracias a orphanRemoval = true en Carrito.items,
        // al guardar el carrito, Hibernate eliminará el CarritoItem huérfano.
        actualizarTotal(carrito);
        return carritoRepository.save(carrito);
    }

    // =========================================================
    // Limpiar carrito
    // =========================================================
    @Override
    public void clearCarrito(Long usuarioId) {
        Carrito carrito = getCarritoActivo(usuarioId);

        carrito.getItems().clear();
        carrito.setTotal(0.0);

        carritoRepository.save(carrito);
    }

    // =========================================================
    // Cálculo de total
    // =========================================================
    private void actualizarTotal(Carrito carrito) {
        double total = carrito.getItems().stream()
                .mapToDouble(item ->
                        (item.getPrecioUnitario() != null ? item.getPrecioUnitario() : 0.0)
                                * (item.getCantidad() != null ? item.getCantidad() : 1)
                )
                .sum();

        carrito.setTotal(total);
    }
}
