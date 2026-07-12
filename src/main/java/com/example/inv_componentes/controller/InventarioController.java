package com.example.inv_componentes.controller;

import com.example.inv_componentes.client.ProductoClient;
import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.model.Producto;
import com.example.inv_componentes.service.InventarioService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventario Controller", description = "Gestión de Stock de productos en bodega")
public class InventarioController {

    private final ProductoClient productoClient;

    @Autowired
    private final InventarioService service;



    @PostMapping
    @Operation(summary = "Crear inventario", description = "Crea un registro de inventario para un producto si este existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro de inventario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class,
                                    example = "{\"idInventario\": 1, \"idProducto\": 102, \"nombreProducto\": \"Mouse Gamer Wireless\", \"descripcion\": \"Mouse óptico 16000 DPI\", \"stockActual\": 50}"))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o fallas en la validación del esquema"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el microservicio externo o error en la vinculación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la persistencia")
    })
    public ResponseEntity<Inventario> crearInventario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para el alta de inventario",
                    content = @Content(schema = @Schema(implementation = Inventario.class,
                            example = "{\"idProducto\": 102, \"stockActual\": 50}")))
            @Valid @RequestBody Inventario inventario) {

        log.info("Creando inventario para el producto: {}", inventario.getIdProducto());

        try {
            Producto producto = productoClient
                    .obtenerProducto(inventario.getIdProducto())
                    .block();

            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            inventario.setNombreProducto(producto.getNombre());
            inventario.setDescripcion(producto.getDescripcion());

            Inventario guardado = service.save(inventario);

            guardado.add(
                    linkTo(methodOn(InventarioController.class)
                            .crearInventario(null))
                            .withSelfRel()
            );

            guardado.add(
                    linkTo(methodOn(InventarioController.class)
                            .listar())
                            .withRel("listar")
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(guardado);

        } catch (Exception e) {
            log.error("Error al crear inventario o producto no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar inventario", description = "Obtiene todos los registros de inventario con información de los productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "500", description = "Error interno al recuperar el listado general de almacén")
    })
    public ResponseEntity<List<Inventario>> listar() {

        log.info("Listando todos los items de inventario");

        List<Inventario> listaInventario = service.listar();

        listaInventario.forEach(item -> {

            Producto producto = productoClient
                    .obtenerProducto(item.getIdProducto())
                    .block();

            if (producto != null) {
                item.setNombreProducto(producto.getNombre());
                item.setDescripcion(producto.getDescripcion());
            }

            item.add(
                    linkTo(methodOn(InventarioController.class)
                            .listar())
                            .withSelfRel()
            );

            item.add(
                    linkTo(methodOn(InventarioController.class)
                            .crearInventario(null))
                            .withRel("crear")
            );
        });

        return ResponseEntity.ok(listaInventario);
    }
    @PutMapping("/descontar")
    @Operation(summary = "Descontar stock", description = "Descuenta una cantidad específica de stock de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente (Sin Cuerpo)"),
            @ApiResponse(responseCode = "400", description = "Cantidad a descontar inválida o saldo insuficiente en bodega"),
            @ApiResponse(responseCode = "404", description = "El ID del producto no registra entradas de inventario activas"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la actualización de stock")
    })
    public ResponseEntity<Void> descontarStock(@RequestParam Long idProducto, @RequestParam int cantidad){
        log.info("Descontando stock para producto: {}, cantidad: {}", idProducto, cantidad);
        service.descontarStock(idProducto, cantidad);
        return ResponseEntity.ok().build();
    }
}
