package com.example.demo.controller.v1;

import com.example.demo.model.entity.Carrito;
import com.example.demo.service.CarritoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> getCarrito(@PathVariable Long usuarioId) {
        try {
            Carrito carrito = carritoService.getCarritoActivo(usuarioId);
            return ResponseEntity.ok(carrito);
        } catch (EntityNotFoundException e) {
            // si no tiene carrito, puedes devolver 404 o crear uno vacío
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<Carrito> addItem(
            @PathVariable Long usuarioId,
            @RequestBody AddItemRequest request
    ) {
        Carrito carrito = carritoService.addItem(
                usuarioId,
                request.getProductoId(),
                request.getCantidad()
        );
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<Carrito> updateItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemRequest request
    ) {
        Carrito carrito = carritoService.updateItemCantidad(
                usuarioId,
                itemId,
                request.getCantidad()
        );
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<Carrito> removeItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId
    ) {
        Carrito carrito = carritoService.removeItem(usuarioId, itemId);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> clearCarrito(@PathVariable Long usuarioId) {
        carritoService.clearCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ==== DTOs simples para requests ====

    @Data
    public static class AddItemRequest {
        private Long productoId;
        private Integer cantidad;
    }

    @Data
    public static class UpdateItemRequest {
        private Integer cantidad;
    }
}
