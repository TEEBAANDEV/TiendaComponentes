package com.example.inv_componentes.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_Producto")
    private Long idProducto;

    @Column(name = "nombre_producto")
    private String nombreProducto;

    @Column(name = "descripcion_producto")
    private String descripcion;

    @Column(name = "cantidad")
    private int cantidad;

}
