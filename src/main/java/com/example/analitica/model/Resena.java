package com.example.analitica.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "resenas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa los comentarios en el sistema")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador de comentario",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotNull
    @Column(name = "usuario_id", nullable = false)
    @NotNull
    @Schema(
            description = "Identificador de usuario asociado al comentario",
            example = "2"
    )
    private Long usuarioId;

    @NotNull
    @Column(name = "producto_id", nullable = false)
    @NotNull
    @Schema(
            description = "Identificador de producto asociado a comentario",
            example = "4"
    )
    private Long productoId;

    @NotNull
    @Column(name = "calificacion", nullable = false)
    @NotNull
    @Schema(
            description = "Calificacion asociada a comentario",
            example = "5"
    )
    private Integer calificacion; //

    @NotBlank
    @Column(name = "comentario", length = 500)
    @NotBlank
    @Schema(
            description = "Comentario registrado en el sistema",
            example = "Muy bueno, recomendado"
    )
    private String comentario;

    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
}
