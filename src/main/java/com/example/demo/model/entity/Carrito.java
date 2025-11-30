package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario dueño del carrito
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Para marcar si es el carrito activo del usuario
    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(
            mappedBy = "carrito",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<CarritoItem> items = new HashSet<>();

    // Total del carrito (se recalcula en el servicio)
    @Builder.Default
    @Column(nullable = false)
    private Double total = 0.0;
}
