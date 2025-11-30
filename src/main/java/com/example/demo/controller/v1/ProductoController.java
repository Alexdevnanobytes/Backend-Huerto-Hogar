package com.example.demo.controller.v1;

import com.example.demo.model.entity.Producto;
import com.example.demo.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET /api/v1/productos
    @GetMapping
    public ResponseEntity<List<Producto>> getAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // GET /api/v1/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/productos/codigo/{codigo}
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> getByCodigo(@PathVariable String codigo) {
        return productoService.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/productos/categoria/{codigoCategoria}
    @GetMapping("/categoria/{codigoCategoria}")
    public ResponseEntity<List<Producto>> getByCategoria(@PathVariable String codigoCategoria) {
        return ResponseEntity.ok(
                productoService.findByCategoriaCodigo(codigoCategoria)
        );
    }

    // POST /api/v1/productos
    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        Producto creado = productoService.save(producto);
        return ResponseEntity.created(
                URI.create("/api/v1/productos/" + creado.getId())
        ).body(creado);
    }

    // PUT /api/v1/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(
            @PathVariable Long id,
            @RequestBody Producto producto
    ) {
        return productoService.findById(id)
                .map(actual -> {
                    actual.setNombre(producto.getNombre());
                    actual.setDescripcion(producto.getDescripcion());
                    actual.setPrecio(producto.getPrecio());
                    actual.setStock(producto.getStock());
                    actual.setCodigo(producto.getCodigo());
                    actual.setImagenUrl(producto.getImagenUrl());
                    actual.setCategoria(producto.getCategoria());
                    Producto guardado = productoService.save(actual);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/v1/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
