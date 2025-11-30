package com.example.demo.controller.v1;

import com.example.demo.model.entity.Orden;
import com.example.demo.service.OrdenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping("/crear-desde-carrito/{usuarioId}")
    public ResponseEntity<Orden> crearDesdeCarrito(
            @PathVariable Long usuarioId,
            @RequestBody CrearOrdenRequest request
    ) {
        Orden orden = ordenService.crearOrdenDesdeCarrito(usuarioId, request.getDireccionEntrega());

        return ResponseEntity.created(
                URI.create("/api/v1/ordenes/" + orden.getId())
        ).body(orden);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ordenService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Orden>> getByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenService.findByUsuario(usuarioId));
    }

    @Data
    public static class CrearOrdenRequest {
        private String direccionEntrega;
    }
}
