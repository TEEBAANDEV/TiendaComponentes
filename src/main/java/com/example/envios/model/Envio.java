package com.example.envios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "envios")
@Schema(description = "Modelo que representa un envío de un recibo a un usuario")
public class Envio extends RepresentationModel<Envio> {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   @Schema(description = "ID único del envío", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
   private Long id;

   @Column(name = "id_recibo")
   @Schema(description = "ID del recibo asociado al envío", example = "12")
   private Long idRecibo;

   @Column(name = "id_usuario")
   @Schema(description = "ID del usuario que recibirá el envío", example = "5")
   private Long idUsuario;

   @Column(name = "direccion_destino")
   @Schema(description = "Dirección de destino del envío", example = "Calle Falsa 123")
   private String direccionDestino;

   @Column(name = "empresa_transporte")
   @Schema(description = "Empresa encargada del transporte", example = "DHL")
   private String empresaTransporte;

   @Column(name = "codigo_seguimiento")
   @Schema(description = "Código de seguimiento del envío", example = "TRK-A8B9C10D")
   private String codigoSeguimiento;

   @Column(name = "estado_envio")
   @Schema(description = "Estado actual del envío", example = "EN_TRANSITO")
   private String estadoEnvio;

   @Column(name = "fecha_actualizacion")
   @Schema(description = "Fecha de la última actualización del envío", example = "2026-06-18")
   private LocalDate fechaActalizacion;

   @Column(name = "fecha_despacho")
   @Schema(description = "Fecha y hora del despacho del envío", example = "2026-06-18T10:15:30")
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
