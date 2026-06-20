package com.example.inv_cliente.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "Carrito_items")
@Schema(description = "Modelo que representa un ítem dentro del carrito del cliente")
public class Inventario_cliente extends RepresentationModel<Inventario_cliente> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del ítem en el carrito", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El id de usuario es obligatorio")
    @Schema(description = "Identificador único del usuario dueño del carrito", example = "10")
    private Long idUsuario;

    @Column(nullable = false)
    @NotNull(message = "El id del producto es obligarorio")
    @Schema(description = "Identificador único del producto en el carrito", example = "45")
    private Long idProducto;

    @Column(nullable = false)
    @Schema(description = "Nombre del producto", example = "Memoria RAM DDR4 16GB")
    private String nombreProducto;

    @Column(nullable = false)
    @Schema(description = "Descripción del producto", example = "Memoria RAM de alta velocidad")
    private String descripcionProducto;

    @Column(nullable = false)
    @Min(value = 1,message = "La cantidad minima del producto es 1")
    @Schema(description = "Cantidad del producto en el carrito", example = "2")
    private Integer cantidad;

}
