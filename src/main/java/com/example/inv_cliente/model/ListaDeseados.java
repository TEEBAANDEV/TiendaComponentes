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
@Table(name = "lista_deseos_items")
public class ListaDeseados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El id de usuario es obligatorio")
    private Long idUsuario;
    @Column(nullable = false)
    @NotNull(message = "El id del producto es obligatorio")
    private Long idProducto;
    @Column(nullable = false)
    private String nombreProducto;
    @Column(nullable = false)
    private String descripcionProducto;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad mínima debe ser 1") // Evita vacíos lógicos o ceros
    private Integer cantidad;

}
