package com.example.inv_cliente.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Carrito_items")
public class Inventario_cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private Long idUsuario;

    @Column(nullable = false)
    @NotBlank
    private Long idProducto;
    @Column(nullable = false)
    private String nombreProducto;
    @Column(nullable = false)
    private String descripcionProducto;
    @Column(nullable = false)
    @NotBlank
    private int cantidad;

}
