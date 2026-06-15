package com.example.inv_cliente.model;

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
@Table(name = "lista_deseos_items")
@Schema(description = "Entidad que representa un artículo o ítem dentro de la lista de deseos de un cliente")
public class ListaDeseados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del ítem en la lista de deseos generado automáticamente",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El id de usuario es obligatorio")
    @Schema(
            description = "Identificador único del usuario dueño de la lista de deseos",
            example = "15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idUsuario;

    @Column(nullable = false)
    @NotNull(message = "El id del producto es obligatorio")
    @Schema(
            description = "Identificador único del producto agregado a la lista",
            example = "102",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idProducto;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 255)
    @Column(nullable = false)
    @Schema(
            description = "Nombre comercial descriptivo del producto al momento de agregarlo",
            example = "Teclado Mecánico RGB Gamer",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombreProducto;

    @NotBlank(message = "La descripción del producto no puede estar vacía")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    @Schema(
            description = "Detalles básicos o especificaciones del producto guardado",
            example = "Teclado con switches red, distribución en español y retroiluminación custom.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcionProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    @Column(nullable = false)
    @Schema(
            description = "Cantidad de unidades del mismo producto que el usuario desea adquirir",
            example = "2",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;
}
