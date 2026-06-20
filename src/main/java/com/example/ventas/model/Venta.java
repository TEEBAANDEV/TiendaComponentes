package com.example.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "ventas")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una venta en el sistema")
public class Venta extends RepresentationModel<Venta> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la venta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario comprador", example = "5")
    private Long idUsuario;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "venta_id", nullable = false)
    @Schema(description = "Detalles de los productos vendidos")
    private List<DetalleVenta> detalles;

    @Column(nullable = false)
    @Schema(description = "Monto total de la venta", example = "99.99")
    private Double total;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora en la que se realizó la venta", example = "2026-06-18T10:15:30")
    private LocalDateTime fecha;

    @Column(nullable = false)
    @Schema(description = "Estado actual de la venta", example = "PAGADO")
    private String estado;
}
