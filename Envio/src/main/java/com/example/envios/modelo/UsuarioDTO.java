package com.example.envios.modelo;


import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;
    private String username;
    private String role;
    private String direccion;
}
