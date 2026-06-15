package com.example.inv_componentes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario")
@Schema(description = "Entidad que representa el stock o disponibilidad física de un producto en el inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(
            description = "Identificador único del registro de inventario generado automáticamente",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    @Column(name = "id_Producto", nullable = false)
    @Schema(
            description = "Identificador único del producto asociado al stock",
            example = "101",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idProducto;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 255)
    @Column(name = "nombre_producto", nullable = false)
    @Schema(
            description = "Nombre comercial descriptivo del producto registrado",
            example = "Memoria RAM DDR5 16GB",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombreProducto;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500)
    @Column(name = "descripcion_producto", length = 500, nullable = false)
    @Schema(
            description = "Detalles técnicos o especificaciones del componente",
            example = "Frecuencia de 5200MHz, CL40, con disipador de aluminio integrado.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Column(name = "cantidad", nullable = false)
    @Schema(
            description = "Número de unidades físicas disponibles en bodega",
            example = "45",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;
}