package com.example.componentesv2.controller;

import com.example.componentesv2.model.Producto;
import com.example.componentesv2.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    @Test
    @DisplayName("Given una lista de productos, When se consulta el endpoint global, Then retorna 200 OK con links en Coleccion HATEOAS")
    void deberiaListarProductosController() {
        // Given
        Producto p = new Producto();
        p.setId(5L);
        when(service.listar()).thenReturn(List.of(p));

        // When
        ResponseEntity<CollectionModel<Producto>> response = controller.listar();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID existente, When se busca producto, Then añade metadatos relacionales y retorna 200 OK")
    void deberiaBuscarPorIdController() {
        // Given
        Long id = 5L;
        Producto p = new Producto();
        p.setId(id);
        when(service.findById(id)).thenReturn(Optional.of(p));

        // When
        ResponseEntity<Producto> response = controller.findById(id);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID invalido, When se busca por ID, Then retorna una respuesta 404 Not Found")
    void deberiaRetornar404CuandoProductoNoExiste() {
        // Given
        Long id = 999L;
        when(service.findById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Producto> response = controller.findById(id);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Given un nuevo producto, When se envia al endpoint POST, Then registra el item y retorna 201 Created")
    void deberiaAgregarProductoController() {
        // Given
        Producto input = new Producto();
        input.setNombre("Procesador i7");

        Producto guardado = new Producto();
        guardado.setId(6L);
        guardado.setNombre("Procesador i7");

        when(service.agregarProducto(any(Producto.class))).thenReturn(guardado);

        // When
        ResponseEntity<Producto> response = controller.agregarProducto(input);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(6L, response.getBody().getId());
    }

    @Test
    @DisplayName("Given un ID existente, When se solicita borrar, Then elimina el registro y devuelve un estado 244 No Content")
    void deberiaEliminarProductoController() {
        // Given
        Long id = 5L;
        Producto p = new Producto();
        p.setId(id);

        when(service.findById(id)).thenReturn(Optional.of(p));
        doNothing().when(service).eliminarProducto(id);

        // When
        ResponseEntity<Void> response = controller.eliminarProducto(id);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).eliminarProducto(id);
    }
}