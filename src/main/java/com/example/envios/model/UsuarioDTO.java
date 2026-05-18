package com.example.envios.model;


import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;
    private String username;
    private String role;
    private String direccion;
}
