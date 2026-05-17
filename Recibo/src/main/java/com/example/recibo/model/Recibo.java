package com.example.recibo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recibo")
public class Recibo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRecibo")
    private Long idRecibo;
    @Column(name = "idVenta")
    private Long idVenta;
    @Column(name = "idUsuario")
    private Long idUsuario;
    @Column(name = "nombreProducto")
    private String nombreProducto;
    @Column(name = "montoTotal")
    private Double montoTotal;
    @Column(name = "metodoPago")
    private String metodoPago;
    @Column(name = "fechaEmision")
    private LocalDateTime fechaEmision;
}
