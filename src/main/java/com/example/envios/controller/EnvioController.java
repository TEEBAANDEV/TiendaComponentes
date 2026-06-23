package com.example.envios.controller;

import com.example.envios.Service.EnvioService;
import com.example.envios.client.ReciboClient;
import com.example.envios.client.UsuarioClient;
import com.example.envios.model.Envio;
import com.example.envios.model.ReciboDTO;
import com.example.envios.model.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/envio")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Envío Controller", description = "Endpoints para la gestión y despacho de envíos")
public class EnvioController {

    @Autowired
    private final EnvioService service;
    
    @Autowired
    private final ReciboClient reciboClient;
    
    @Autowired
    private final UsuarioClient usuarioClient;

    private Envio addLinks(Envio envio) {

        if (envio == null || envio.getId() == null) {
            return envio;
        }

        envio.add(
                linkTo(methodOn(EnvioController.class)
                        .buscarPorId(envio.getId()))
                        .withSelfRel()
        );

        envio.add(
                linkTo(methodOn(EnvioController.class)
                        .listar())
                        .withRel("listar_envios")
        );

        return envio;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los envíos",
            description = "Obtiene la lista completa de todos los envíos registrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de envíos recuperada exitosamente")
    })
    public ResponseEntity<List<Envio>> listar() {

        log.info("Solicitud para listar todos los envíos recibida");

        List<Envio> envios = service.listar();

        envios.forEach(envio -> {

            envio.add(
                    linkTo(methodOn(EnvioController.class)
                            .buscarPorId(envio.getId()))
                            .withSelfRel()
            );

            envio.add(
                    linkTo(methodOn(EnvioController.class)
                            .listar())
                            .withRel("todos")
            );
        });

        return ResponseEntity.ok(envios);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar envío por ID",
            description = "Obtiene los detalles de un envío específico según su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    public ResponseEntity<Envio> buscarPorId(@PathVariable Long id) {

        log.info("Buscando envío con ID: {}", id);

        return service.findById(id)
                .map(envio -> {

                    envio.add(
                            linkTo(methodOn(EnvioController.class)
                                    .buscarPorId(id))
                                    .withSelfRel()
                    );

                    envio.add(
                            linkTo(methodOn(EnvioController.class)
                                    .listar())
                                    .withRel("todos")
                    );

                    return ResponseEntity.ok(envio);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/generar/{idRecibo}")
    @Operation(summary = "Despachar envío", description = "Genera y registra el envío a partir del ID de un recibo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envío generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Recibo o dirección de usuario no encontrada"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible debido a un error")
    })
    public ResponseEntity<Envio> despacharenvio(@PathVariable Long idRecibo) {

        try {

            log.info("Iniciando despacho de envío para el recibo ID: {}", idRecibo);

            ReciboDTO reciboDTO = reciboClient.obtenerRecibo(idRecibo).block();

            if (reciboDTO == null) {
                return ResponseEntity.notFound().build();
            }

            UsuarioDTO usuario = usuarioClient.obtenerUsuario(reciboDTO.getIdUsuario()).block();

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Envio envio = new Envio();
            envio.setIdRecibo(idRecibo);
            envio.setIdUsuario(reciboDTO.getIdUsuario());
            envio.setDireccionDestino(usuario.getDireccion());

            // 👇 lógica real (ejemplo)
            envio.setEmpresaTransporte("Starken");
            envio.setCodigoSeguimiento(UUID.randomUUID().toString());
            envio.setEstadoEnvio("PROCESANDO_LOGISTICA");
            envio.setFechaDespacho(LocalDateTime.now());

            Envio guardado = service.save(envio).block();

            guardado.add(
                    linkTo(methodOn(EnvioController.class)
                            .buscarPorId(guardado.getId()))
                            .withSelfRel()
            );

            guardado.add(
                    linkTo(methodOn(EnvioController.class)
                            .listar())
                            .withRel("envios")
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);

        } catch (Exception e) {

            log.error("Error al crear envio: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
