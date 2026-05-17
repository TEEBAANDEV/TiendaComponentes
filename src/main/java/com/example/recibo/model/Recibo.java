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
    @Column(name = "id_recibo")
    private Long idRecibo;
    @Column(name = "id_venta")
    private Long idVenta;
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "nombre_producto")
    private String nombreProducto;
    @Column(name = "monto_total")
    private Double montoTotal;
    @Column(name = "metodo_pago")
    private String metodoPago;
    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;
}
