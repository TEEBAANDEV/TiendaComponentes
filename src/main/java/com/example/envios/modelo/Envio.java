package com.example.envios.modelo;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envio {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idRecibido;
   private Long idVenta;
   private Long idUsuario;
   private String direccionDestino;
   private String empresaTransporte;
   private String codigoSeguimiento;
   private String estado;
   private LocalDate fechaActalizacion;

}
