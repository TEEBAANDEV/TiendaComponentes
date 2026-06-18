package com.example.componentesv2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producto")
@Schema(description = "Modelo que representa un Producto en el inventario de la tienda")
public class Producto extends RepresentationModel<Producto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Schema(description = "Nombre único del producto", example = "Memoria RAM DDR5 32GB")
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    @Schema(description = "Descripción detallada del producto", example = "Memoria RAM de alta velocidad para gaming y diseño")
    private String descripcion;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "Precio del producto en USD", example = "149.99")
    private Double precio;
}
