package com.example.inv_cliente.service;


import com.example.inv_cliente.client.InventarioClient;
import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.model.InventarioDTO;
import com.example.inv_cliente.repository.InventarioCliRespository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioCliServiceTest {

    @Mock
    private InventarioCliRespository repository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private InventarioCliService service;

    @Test
    @DisplayName("Given que hay stock suficiente, When se agrega al carrito, Then guarda el item reactivamente")
    void deberiaAgregarAlCarritoConStockSuficiente() {
        // Given
        Inventario_cliente item = new Inventario_cliente();
        item.setIdProducto(10L);
        item.setCantidad(2);

        InventarioDTO stockMock = new InventarioDTO();
        stockMock.setCantidad(10);

        // When
        when(inventarioClient.obtenerStock(10L)).thenReturn(Mono.just(stockMock));
        when(repository.save(any(Inventario_cliente.class))).thenReturn(item);
        Mono<Inventario_cliente> resultadoMono = service.agregarAlCarrito(item);

        // Then
        StepVerifier.create(resultadoMono)
                .assertNext(resultado -> {
                    assertNotNull(resultado);
                    assertEquals(10L, resultado.getIdProducto());
                    assertEquals(2, resultado.getCantidad());
                })
                .verifyComplete();

        verify(inventarioClient, times(1)).obtenerStock(10L);
        verify(repository, times(1)).save(item);
    }

    @Test
    @DisplayName("Given stock insuficiente en el inventario remoto, When se intenta agregar, Then arroja una excepción BAD_REQUEST")
    void deberiaLanzarErrorCuandoStockEsInsuficiente() {
        // Given
        Inventario_cliente item = new Inventario_cliente();
        item.setIdProducto(10L);
        item.setCantidad(5);

        InventarioDTO stockMock = new InventarioDTO();
        stockMock.setCantidad(2);

        // When
        when(inventarioClient.obtenerStock(10L)).thenReturn(Mono.just(stockMock));
        Mono<Inventario_cliente> resultadoMono = service.agregarAlCarrito(item);

        // Then
        StepVerifier.create(resultadoMono)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository, never()).save(any(Inventario_cliente.class));
    }

    @Test
    @DisplayName("Given un ID de usuario, When se consulta su carrito, Then retorna los registros asociados de la base de datos")
    void deberiaObtenerCarritoPorUsuario() {

        // Given
        Long idUsuario = 1L;
        Inventario_cliente item = new Inventario_cliente();
        item.setIdUsuario(idUsuario);
        // When
        when(repository.findByIdUsuario(idUsuario)).thenReturn(List.of(item));
        List<Inventario_cliente> resultado = service.obtenerCarritoPorUsuario(idUsuario);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByIdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Given un ID de registro, When se elimina del carrito, Then llama al repositorio para borrarlo")
    void deberiaEliminarDelCarrito() {
        // Given
        Long idItem = 5L;
        doNothing().when(repository).deleteById(idItem);

        // When
        service.eliminarDelCarrito(idItem);

        // Then
        verify(repository, times(1)).deleteById(idItem);
    }

    @Test
    @DisplayName("Given un ID de usuario, When se vacía el carrito, Then elimina todos sus ítems asociados")
    void deberiaVaciarCarritoPorUsuario() {
        // Given
        Long idUsuario = 1L;
        Inventario_cliente item = new Inventario_cliente();
        List<Inventario_cliente> itemsMock = List.of(item);
        // When
        when(repository.findByIdUsuario(idUsuario)).thenReturn(itemsMock);
        doNothing().when(repository).deleteAll(itemsMock);
        service.vaciarCarritoPorUsuario(idUsuario);

        // Then
        verify(repository, times(1)).findByIdUsuario(idUsuario);
        verify(repository, times(1)).deleteAll(itemsMock);
    }

    @Test
    @DisplayName("Given que existen registros en el sistema, When se listan todos, Then retorna el total")
    void deberiaListarTodosLosElementos() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Inventario_cliente> resultado = service.listar();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).findAll();
    }
}