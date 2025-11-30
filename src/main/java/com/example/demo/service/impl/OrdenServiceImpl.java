package com.example.demo.service.impl;

import com.example.demo.model.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.OrdenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenItemRepository ordenItemRepository;
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Orden crearOrdenDesdeCarrito(Long usuarioId, String direccionEntrega) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no tiene carrito activo"));

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        // calcular total
        Double total = carrito.getItems().stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();

        Orden orden = Orden.builder()
                .usuario(usuario)
                .fechaCreacion(LocalDateTime.now())
                .total(total)
                .estado("CREADA")
                .direccionEntrega(direccionEntrega)
                .build();

        Orden ordenGuardada = ordenRepository.save(orden);

        var ordenItems = new HashSet<OrdenItem>();

        carrito.getItems().forEach(ci -> {
            OrdenItem oi = OrdenItem.builder()
                    .orden(ordenGuardada)
                    .producto(ci.getProducto())
                    .cantidad(ci.getCantidad())
                    .precioUnitario(ci.getPrecioUnitario())
                    .build();
            ordenItems.add(ordenItemRepository.save(oi));
        });

        ordenGuardada.setItems(ordenItems);

        // limpiar carrito y marcarlo como inactivo
        carrito.getItems().clear();
        carrito.setActivo(false);
        carritoRepository.save(carrito);

        return ordenGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Orden findById(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return ordenRepository.findByUsuario(usuario);
    }
}
