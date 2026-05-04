package com.example.inv_cliente.model;


import lombok.Data;

@Data
public class Producto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
}
