package com.example.reportes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa un reporte o informe administrativo generado en el sistema")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del reporte autogenerado por la base de datos",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotNull(message = "El ID del recibo es obligatorio")
    @Column(name = "id_Recibo", nullable = false)
    @Schema(
            description = "Identificador del recibo asociado que originó o justifica este reporte",
            example = "2540",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idRecibo;

    @NotBlank(message = "El nombre del reporte no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Título o nombre descriptivo breve del reporte",
            example = "Reporte Mensual de Despachos Logísticos",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(columnDefinition = "TEXT")
    @Schema(
            description = "Detalle extenso o cuerpo analítico que contiene la información del reporte",
            example = "El presente informe detalla las anomalías encontradas en las entregas de la región...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Column(name = "tipo_reporte", nullable = false)
    @Schema(
            description = "Categoría o clasificación de la naturaleza del reporte",
            example = "FINANCIERO",
            allowableValues = {"FINANCIERO", "LOGISTICA", "AUDITORIA", "RECLAMO", "ESTADISTICO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoReporte;

    @Column(name = "fecha_creacion")
    @Schema(
            description = "Fecha y hora exacta en la que el sistema procesó y creó el reporte",
            example = "2026-06-15T18:10:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime fechaCreacion;

    @Column(name = "estado")
    @Schema(
            description = "Estado administrativo actual del ciclo de vida del reporte",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO", "ARCHIVADO", "EN_REVISION"}
    )
    private String estado;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = "ACTIVO";
    }
}