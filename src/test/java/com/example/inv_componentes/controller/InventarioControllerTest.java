package com.example.inv_componentes.controller;


import com.example.inv_componentes.client.ProductoClient;

import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.model.Producto;
import com.example.inv_componentes.service.InventarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioService service;

    @InjectMocks
    private InventarioController controller;

    @Test
    @DisplayName("Given un producto valido en catalogo, When se crea el inventario, Then vincula datos y retorna 201 Created con HATEOAS")
    void deberiaCrearInventarioController() {
        // Given
        Inventario input = new Inventario();
        input.setIdProducto(102L);
        input.setCantidad(50);

        Producto productoMock = new Producto();
        productoMock.setNombre("Teclado Mecanico");
        productoMock.setDescripcion("Switches Red");

        Inventario guardadoMock = new Inventario();
        guardadoMock.setIdProducto(1L);
        guardadoMock.setIdProducto(102L);

        when(productoClient.obtenerProducto(102L)).thenReturn(Mono.just(productoMock));
        when(service.save(any(Inventario.class))).thenReturn(guardadoMock);

        // When
        ResponseEntity<Inventario> response = controller.crearInventario(input);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given que el cliente de productos retorna vacio, When se crea inventario, Then el catch captura y retorna 404")
    void deberiaRetornar404CuandoProductoNoExisteEnCatalogo() {
        // Given
        Inventario input = new Inventario();
        input.setIdProducto(999L);

        when(productoClient.obtenerProducto(999L)).thenReturn(Mono.empty());

        // When
        ResponseEntity<Inventario> response = controller.crearInventario(input);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, never()).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Given que existen registros en stock, When se listan, Then consulta la informacion remota de cada producto y retorna 200 OK")
    void deberiaListarInventarioConDetallesDeProducto() {
        // Given
        Inventario item = new Inventario();
        item.setIdProducto(102L);

        Producto productoMock = new Producto();
        productoMock.setNombre("Teclado Mecanico");

        when(service.listar()).thenReturn(List.of(item));
        when(productoClient.obtenerProducto(102L)).thenReturn(Mono.just(productoMock));

        // When
        ResponseEntity<List<Inventario>> response = controller.listar();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Teclado Mecanico", response.getBody().get(0).getNombreProducto());
    }

    @Test
    @DisplayName("Given parametros correctos, When se solicita descontar, Then invoca el servicio y retorna 200 OK")
    void deberiaLlamarAlMetodoDescontarStock() {
        // Given
        Long idProducto = 102L;
        int cantidad = 2;
        doNothing().when(service).descontarStock(idProducto, cantidad);

        // When
        ResponseEntity<Void> response = controller.descontarStock(idProducto, cantidad);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).descontarStock(idProducto, cantidad);
    }
}