package com.example.ventas.model;


import lombok.Data;

@Data
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;

}
