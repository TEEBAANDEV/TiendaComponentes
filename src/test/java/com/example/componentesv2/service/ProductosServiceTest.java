package com.example.componentesv2.service;


import com.example.componentesv2.model.Producto;
import com.example.componentesv2.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    @Test
    @DisplayName("Given que existen productos en catalogo, When se listan, Then retorna todos los registros de la BD")
    void deberiaListarTodosLosProductos() {
        // Given
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Tarjeta de Video");
        when(repository.findAll()).thenReturn(List.of(p));

        // When
        List<Producto> resultado = service.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tarjeta de Video", resultado.get(0).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given un ID valido, When se busca por ID, Then retorna el Optional con el producto correspondiente")
    void deberiaBuscarProductoPorIdExistente() {
        // Given
        Long id = 5L;
        Producto p = new Producto();
        p.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(p));

        // When
        Optional<Producto> resultado = service.findById(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given un objeto Producto valido, When se agrega, Then se persiste de forma imperativa en la BD")
    void deberiaAgregarProductoExitosamente() {
        // Given
        Producto p = new Producto();
        p.setNombre("Memoria RAM 16GB");
        when(repository.save(any(Producto.class))).thenReturn(p);

        // When
        Producto resultado = service.agregarProducto(p);

        // Then
        assertNotNull(resultado);
        assertEquals("Memoria RAM 16GB", resultado.getNombre());
        verify(repository, times(1)).save(p);
    }

    @Test
    @DisplayName("Given un ID existente, When se solicita eliminar, Then invoca de manera efectiva el delete del repositorio")
    void deberiaEliminarProductoSiExiste() {
        // Given
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // When
        service.eliminarProducto(id);

        // Then
        verify(repository, times(1)).existsById(id);
        verify(repository, times(1)).deleteById(id);
    }
}