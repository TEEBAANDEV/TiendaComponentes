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
   @Column(name = "id")
   private Long id;
   @Column(name = "id_recibo")
   private Long idRecibo;
   @Column(name = "id_usuario")
   private Long idUsuario;
   @Column(name = "direccion_destino")
   private String direccionDestino;
   @Column(name = "empresa_transporte")
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
