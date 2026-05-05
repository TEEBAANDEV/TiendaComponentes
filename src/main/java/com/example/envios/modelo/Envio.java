package com.example.envios.modelo;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "envio")
public class Envio {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private Long idRecibo;
   private Long idUsuario;
   private String direccionDestino;
   private String empresaTransporte;
   @Column(name = "codigo_seguimiento")
   private String codigoSeguimiento;
   private String estadoEnvio;
   private LocalDate fechaActalizacion;

   private LocalDateTime fechaDespacho;

   @PrePersist
   protected void onCreate() {
      if (this.estadoEnvio == null) this.estadoEnvio = "PENDIENTE";
      if (this.fechaDespacho == null) this.fechaDespacho = LocalDateTime.now();
      if (this.codigoSeguimiento == null) {
         this.codigoSeguimiento = "TRK-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
      }
   }

}
