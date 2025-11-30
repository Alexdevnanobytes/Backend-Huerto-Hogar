package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ej: "Verduras", "Frutas", "Producto orgánico"
    @Column(nullable = false, length = 100)
    private String nombre;

    // Ej: "VR", "FR", "PO"
    @Column(nullable = false, unique = true, length = 10)
    private String codigo;
}
