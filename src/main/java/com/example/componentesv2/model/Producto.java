package com.example.componentesv2.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producto")
@Schema(description = "Entidad que representa un producto o componente tecnológico disponible en el catálogo comercial")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del producto generado automáticamente por la base de datos",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255)
    @Column(unique = true, nullable = false)
    @Schema(
            description = "Nombre comercial único del producto o componente",
            example = "Procesador AMD Ryzen 7 7800X3D",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @NotBlank(message = "La descripción del producto no puede estar vacía")
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    @Schema(
            description = "Especificaciones técnicas detalladas y características del artículo",
            example = "8 núcleos, 16 hilos, 4.2GHz base, 96MB L3 Cache V-Cache, socket AM5.",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotNull(message = "El precio del producto es obligatorio")
    @Min(value = 0, message = "El precio del producto no puede ser negativo")
    @Column(nullable = false)
    @Schema(
            description = "Precio unitario bruto de venta del producto en el comercio",
            example = "449990.00",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double precio;
}