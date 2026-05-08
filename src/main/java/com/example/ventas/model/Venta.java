package com.example.ventas.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "ventas")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario",nullable = false)
    private Long idUsuario;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "venta_id", nullable = false)
    private List<DetalleVenta> detalles;

    @Column(nullable = false)
    private Double total; //cambio de emergencia supongo

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String estado;
}
