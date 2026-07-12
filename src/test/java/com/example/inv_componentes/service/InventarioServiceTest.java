package com.example.inv_componentes.service;


import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.repository.InventarioRespository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRespository repository;

    @InjectMocks
    private InventarioService service;

    @Test
    @DisplayName("Given un objeto Inventario, When se guarda, Then retorna el objeto persistido")
    void deberiaGuardarInventario() {
        // Given
        Inventario inventario = new Inventario();
        inventario.setIdProducto(102L);
        inventario.setCantidad(50);

        when(repository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        Inventario resultado = service.save(inventario);

        // Then
        assertNotNull(resultado);
        assertEquals(50, resultado.getCantidad());
        verify(repository, times(1)).save(inventario);
    }

    @Test
    @DisplayName("Given registros en la base de datos, When se listan, Then retorna la lista completa")
    void deberiaListarTodoElInventario() {
        // Given
        Inventario item = new Inventario();
        when(repository.findAll()).thenReturn(List.of(item));

        // When
        List<Inventario> resultado = service.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given stock suficiente, When se descuenta stock, Then actualiza restando la cantidad comprada")
    void deberiaDescontarStockExitosamente() {
        // Given
        Long idProducto = 102L;
        Inventario inventario = new Inventario();
        inventario.setIdProducto(idProducto);
        inventario.setCantidad(20);

        when(repository.findByIdProducto(idProducto)).thenReturn(Optional.of(inventario));
        when(repository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        service.descontarStock(idProducto, 5);

        // Then
        assertEquals(15, inventario.getCantidad()); // 20 - 5 = 15
        verify(repository, times(1)).save(inventario);
    }

    @Test
    @DisplayName("Given stock insuficiente, When se intenta descontar stock, Then lanza una excepcion BAD_REQUEST")
    void deberiaLanzarBadRequestCuandoStockEsInsuficiente() {
        // Given
        Long idProducto = 102L;
        Inventario inventario = new Inventario();
        inventario.setIdProducto(idProducto);
        inventario.setCantidad(3); // Solo hay 3 disponibles

        when(repository.findByIdProducto(idProducto)).thenReturn(Optional.of(inventario));

        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            service.descontarStock(idProducto, 10); // Solicita 10
        });
        verify(repository, never()).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Given un producto que no existe en bodega, When se intenta descontar, Then lanza una excepcion NOT_FOUND")
    void deberiaLanzarNotFoundCuandoProductoNoExiste() {
        // Given
        Long idProducto = 999L;
        when(repository.findByIdProducto(idProducto)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            service.descontarStock(idProducto, 1);
        });
    }
}

