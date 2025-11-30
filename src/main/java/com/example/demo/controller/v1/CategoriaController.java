package com.example.demo.controller.v1;

import com.example.demo.model.entity.Categoria;
import com.example.demo.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Long id) {
        return categoriaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Categoria> create(@RequestBody Categoria categoria) {
        Categoria creada = categoriaService.save(categoria);
        return ResponseEntity.created(
                URI.create("/api/v1/categorias/" + creada.getId())
        ).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(
            @PathVariable Long id,
            @RequestBody Categoria categoria
    ) {
        return categoriaService.findById(id)
                .map(actual -> {
                    actual.setNombre(categoria.getNombre());
                    actual.setCodigo(categoria.getCodigo());
                    Categoria guardada = categoriaService.save(actual);
                    return ResponseEntity.ok(guardada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (categoriaService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
