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
@Schema(description = "Entidad que representa un ítem de la lista de deseos")
public class ListaDeseados extends RepresentationModel<ListaDeseados> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del ítem en la lista de deseos", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El id de usuario es obligatorio")
    @Schema(description = "ID del usuario dueño de este ítem en la lista de deseos", example = "12")
    private Long idUsuario;

    @Column(nullable = false)
    @NotNull(message = "El id del producto es obligatorio")
    @Schema(description = "ID del producto agregado a la lista de deseos", example = "34")
    private Long idProducto;

    @Column(nullable = false)
    @Schema(description = "Nombre del producto", example = "AMD Ryzen 5 5600X")
    private String nombreProducto;

    @Column(nullable = false)
    @Schema(description = "Descripción del producto", example = "Processor 6 cores 12 threads")
    private String descripcionProducto;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad mínima debe ser 1") // Evita vacíos lógicos o ceros
    @Schema(description = "Cantidad solicitada del producto", example = "1")
    private Integer cantidad;

}
