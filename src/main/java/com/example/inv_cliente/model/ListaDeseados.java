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
@Table(name = "lista_deseos_items")
@Schema(description = "Entity representing a wishlist item")
public class ListaDeseados extends RepresentationModel<ListaDeseados> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the wishlist item", example = "1")
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El id de usuario es obligatorio")
    @Schema(description = "ID of the user who owns this wishlist item", example = "12")
    private Long idUsuario;

    @Column(nullable = false)
    @NotNull(message = "El id del producto es obligatorio")
    @Schema(description = "ID of the product added to the wishlist", example = "34")
    private Long idProducto;

    @Column(nullable = false)
    @Schema(description = "Name of the product", example = "AMD Ryzen 5 5600X")
    private String nombreProducto;

    @Column(nullable = false)
    @Schema(description = "Description of the product", example = "Processor 6 cores 12 threads")
    private String descripcionProducto;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad mínima debe ser 1") // Evita vacíos lógicos o ceros
    @Schema(description = "Quantity of the product requested", example = "1")
    private Integer cantidad;

}
