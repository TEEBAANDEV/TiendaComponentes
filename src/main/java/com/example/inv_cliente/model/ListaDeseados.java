package com.example.inv_cliente.model;


import jakarta.persistence.*;
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
public class ListaDeseados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idUsuario;
    @Column(nullable = false)
    private Long idProducto;
    @Column(nullable = false)
    private String nombreProducto;
    @Column(nullable = false)
    private String descripcionProducto;
    @Column(nullable = false)
    private int cantidad;

}
