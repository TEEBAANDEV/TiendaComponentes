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
@Table(name = "lista_deseos_items")
public class ListaDeseados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.validation.constraints.NotNull
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long idUsuario;
    @Column(nullable = false)
    @NotNull
    private Long idProducto;
    @Column(nullable = false)
    @NotBlank
    private String nombreProducto;
    @Column(nullable = false)
    @NotBlank
    private String descripcionProducto;

    @Column(nullable = false)
    @NotNull
    private Integer cantidad;

}
