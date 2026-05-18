package com.example.inv_cliente.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
    @NotNull(message = "El id de usuario es obligatorio")
    private Long idUsuario;
    @Column(nullable = false)
    @NotNull(message = "El id del producto es obligarorio")
    private Long idProducto;
    @Column(nullable = false)
    private String nombreProducto;
    @Column(nullable = false)
    private String descripcionProducto;
    @Column(nullable = false)
    @Min(value = 1,message = "La cantidad minima del producto es 1")
    private Integer cantidad;

}
