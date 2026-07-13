package com.example.recibo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recibo")
@Schema(description = "Representación de un Recibo de venta")
public class Recibo extends RepresentationModel<Recibo> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRecibo")
    @Schema(description = "ID único del recibo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idRecibo;

    @Column(name = "idVenta")
    @Schema(description = "ID de la venta asociada", example = "10")
    private Long idVenta;

    @Column(name = "idUsuario")
    @Schema(description = "ID del usuario comprador", example = "5")
    private Long idUsuario;

    @Column(name = "nombreProducto")
    @Schema(description = "Detalle o glosa de los productos", example = "2x Memoria RAM (Corsair) | 1x SSD (Samsung)")
    private String nombreProducto;

    @Column(name = "montoTotal")
    @Schema(description = "Monto total pagado", example = "150.0")
    private Double montoTotal;

    @Column(name = "metodoPago")
    @Schema(description = "Método de pago utilizado", example = "TARJETA")
    private String metodoPago;

    @Column(name = "fechaEmision")
    @Schema(description = "Fecha de emisión del recibo")
    private LocalDateTime fechaEmision;
}

