package com.example.pago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPago")
    private Long idPago;
    @Column(name = "idVenta")
    private Long idVenta;
    @Column(name = "montoPagado")
    private Long montoPagado;
    @Column(name = "estadoPago")
    private String estadoPago;
    @Column(name = "banco")
    private String banco; //De que banco se hizo la transacción
    @Column(name = "fechaPago")
    private LocalDateTime fechaPago;

}
