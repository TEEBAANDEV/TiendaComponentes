package com.example.envios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "envios")
@Schema(description = "Entidad que representa el estado logístico y despacho de un envío en el sistema")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(
            description = "Identificador único del envío generado automáticamente por la base de datos",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotNull
    @Column(name = "id_recibo", nullable = false)
    @Schema(
            description = "Identificador del recibo o factura que originó el envío",
            example = "1050",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idRecibo;

    @NotNull
    @Column(name = "id_usuario", nullable = false)
    @Schema(
            description = "Identificador del usuario que recibirá el paquete (comprador)",
            example = "45",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idUsuario;

    @NotBlank
    @Size(max = 255)
    @Column(name = "direccion_destino", nullable = false)
    @Schema(
            description = "Dirección física completa estructurada hacia donde se despachará el producto",
            example = "Av. Siempre Viva 742, Melipilla, Chile",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String direccionDestino;

    @Size(max = 100)
    @Column(name = "empresa_transporte", length = 100)
    @Schema(
            description = "Nombre de la empresa externa asignada para la distribución logística",
            example = "Chilexpress",
            maxLength = 100
    )
    private String empresaTransporte;

    @Column(name = "codigo_seguimiento", unique = true)
    @Schema(
            description = "Código único de tracking generado por logística para el rastreo del cliente",
            example = "TRK-A1B2C3D4",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String codigoSeguimiento;

    @Column(name = "estado_envio")
    @Schema(
            description = "Estado actual del ciclo de vida del despacho",
            example = "PROCESANDO_LOGISTICA",
            allowableValues = {"PENDIENTE", "PROCESANDO_LOGISTICA", "DESPACHADO", "EN_RUTA", "ENTREGADO", "CANCELADO"}
    )
    private String estadoEnvio;

    @Column(name = "fecha_actualizacion")
    @Schema(
            description = "Última fecha registrada en la que cambió el estado del envío",
            example = "2026-06-15"
    )
    private LocalDate fechaActalizacion;

    @Column(name = "fecha_despacho")
    @Schema(
            description = "Fecha y hora exacta en la que el paquete salió del centro de distribución",
            example = "2026-06-15T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime fechaDespacho;

    @PrePersist
    protected void onCreate() {
        if (this.estadoEnvio == null) this.estadoEnvio = "PENDIENTE";
        if (this.fechaDespacho == null) this.fechaDespacho = LocalDateTime.now();
        if (this.fechaActalizacion == null) this.fechaActalizacion = LocalDate.now();
        if (this.codigoSeguimiento == null) {
            this.codigoSeguimiento = "TRK-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
