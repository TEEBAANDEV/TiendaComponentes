package com.example.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo que representa un reporte en el sistema")
public class Reporte extends RepresentationModel<Reporte> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del reporte", example = "1")
    private Long id;

    @Column(name = "id_Recibo")
    @Schema(description = "ID del recibo asociado", example = "100")
    private Long idRecibo;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del reporte", example = "Reporte de Venta - Recibo #100")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción del reporte", example = "Detalles de la compra del usuario")
    private String descripcion;

    @Column(name = "tipo_reporte")
    @Schema(description = "Tipo de reporte", example = "Venta_Cliente")
    private String tipoReporte;

    @Column(name = "fecha_creacion")
    @Schema(description = "Fecha de creación del reporte")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Estado del reporte", example = "ACTIVO")
    private String estado;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = "ACTIVO";
    }

}
