package com.example.recibo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recibo")
@Schema(description = "Entidad que representa el recibo o comprobante fiscal de una transacción de venta en el sistema")
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recibo")
    @Schema(
            description = "Identificador único del recibo generado automáticamente por la base de datos",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idRecibo;

    @NotNull(message = "El ID de la venta es obligatorio")
    @Column(name = "id_venta", nullable = false)
    @Schema(
            description = "Identificador de la transacción de venta asociada",
            example = "50012",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idVenta;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    @Schema(
            description = "Identificador del usuario que realizó la compra",
            example = "15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idUsuario;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 255)
    @Column(name = "nombre_producto", nullable = false)
    @Schema(
            description = "Nombre o listado comercial del producto o ítems adquiridos",
            example = "Teclado Mecánico RGB Gamer",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombreProducto;

    @NotNull(message = "El monto total es obligatorio")
    @Min(value = 0, message = "El monto total no puede ser negativo")
    @Column(name = "monto_total", nullable = false)
    @Schema(
            description = "Valor económico total bruto facturado en la transacción",
            example = "49990.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double montoTotal;

    @NotBlank(message = "El método de pago es obligatorio")
    @Column(name = "metodo_pago", nullable = false)
    @Schema(
            description = "Medio financiero utilizado por el cliente para completar el pago",
            example = "TARJETA_CREDITO",
            allowableValues = {"TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA", "EFECTIVO", "PAYPAL"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String metodoPago;

    @Column(name = "fecha_emision")
    @Schema(
            description = "Fecha y hora exacta en la que se generó y emitió el comprobante",
            example = "2026-06-15T19:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime fechaEmision;

    @PrePersist
    protected void onCreate() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDateTime.now();
        }
    }
}