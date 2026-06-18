package com.example.componentesv2.controller;

import com.example.componentesv2.model.Producto;
import com.example.componentesv2.service.ProductoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
@Tag(name = "Productos", description = "Endpoints para la gestión de productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista con todos los productos registrados, incluyendo sus enlaces HATEOAS individuales.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    public ResponseEntity<CollectionModel<Producto>> listar() {
        log.info("Petición recibida para listar todos los productos");
        List<Producto> productos = service.listar();
        
        for (Producto producto : productos) {
            producto.add(linkTo(methodOn(ProductoController.class).findById(producto.getId())).withSelfRel());
        }
        
        Link selfLink = linkTo(methodOn(ProductoController.class).listar()).withSelfRel();
        CollectionModel<Producto> result = CollectionModel.of(productos, selfLink);
        
        log.info("Se han listado {} productos con sus respectivos enlaces HATEOAS", productos.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Permite obtener los detalles de un producto específico mediante su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        log.info("Petición recibida para buscar producto con ID: {}", id);
        return service.findById(id)
                .map(producto -> {
                    producto.add(linkTo(methodOn(ProductoController.class).findById(id)).withSelfRel());
                    producto.add(linkTo(methodOn(ProductoController.class).listar()).withRel("productos"));
                    log.info("Producto encontrado para el ID: {}", id);
                    return ResponseEntity.ok(producto);
                })
                .orElseGet(() -> {
                    log.warn("Producto con ID: {} no encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @Operation(summary = "Agregar un nuevo producto", description = "Permite registrar un nuevo producto en la tienda de componentes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<Producto> agregarProducto(@Valid @RequestBody Producto producto) {
        log.info("Petición recibida para agregar un nuevo producto: {}", producto.getNombre());
        Producto nuevoProducto = service.agregarProducto(producto);
        
        nuevoProducto.add(linkTo(methodOn(ProductoController.class).findById(nuevoProducto.getId())).withSelfRel());
        nuevoProducto.add(linkTo(methodOn(ProductoController.class).listar()).withRel("productos"));
        
        log.info("Producto guardado exitosamente con ID: {}", nuevoProducto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Permite eliminar un producto existente de la base de datos por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("Petición recibida para eliminar producto con ID: {}", id);
        if (service.findById(id).isPresent()) {
            service.eliminarProducto(id);
            log.info("Producto con ID: {} eliminado correctamente", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            log.warn("No se pudo eliminar el producto: ID {} no encontrado", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
