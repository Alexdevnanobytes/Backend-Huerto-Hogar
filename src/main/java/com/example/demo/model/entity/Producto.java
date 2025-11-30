package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ej: "Pimentón rojo"
    @Column(nullable = false, length = 150)
    private String nombre;

    // Ej: "Pimentón fresco y crujiente, ideal para ensaladas"
    @Column(length = 500)
    private String descripcion;

    // Ej: "1200" (puedes cambiar a BigDecimal si quieres más precisión)
    @Column(nullable = false)
    private Double precio;

    // Ej: cantidad disponible
    @Column(nullable = false)
    private Integer stock;

    // Ej: "VR01", "PO01"... esto calza con tu front
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    // Ej: "/img/pimenton-rojo.png"
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
