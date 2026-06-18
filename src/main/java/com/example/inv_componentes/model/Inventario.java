package com.example.inv_componentes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@Table(name = "inventario")
@Schema(description = "Modelo que representa el inventario de un producto")
public class Inventario extends RepresentationModel<Inventario> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Identificador único de la entrada de inventario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "id_Producto")
    @NotNull
    @Schema(description = "ID del producto asociado", example = "101")
    private Long idProducto;

    @Column(name = "nombre_producto")
    @Schema(description = "Nombre del producto obtenido del microservicio de productos", example = "Procesador Intel i7", accessMode = Schema.AccessMode.READ_ONLY)
    private String nombreProducto;

    @Column(name = "descripcion_producto")
    @Schema(description = "Descripción del producto obtenido del microservicio de productos", example = "12 núcleos, 4.8 GHz", accessMode = Schema.AccessMode.READ_ONLY)
    private String descripcion;

    @Column(name = "cantidad")
    @NotNull
    @Schema(description = "Cantidad de unidades en stock", example = "50")
    private Integer cantidad;

}
