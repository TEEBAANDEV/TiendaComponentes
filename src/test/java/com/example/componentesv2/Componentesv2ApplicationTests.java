package com.example.componentesv2;

import com.example.componentesv2.model.Producto;
import com.example.componentesv2.repository.ProductoRepository;
import com.example.componentesv2.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Componentesv2ApplicationTests {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testAgregarProductoExitoso() {
        Producto productoParaGuardar = Producto.builder()
                .nombre("Memoria RAM DDR5 32GB")
                .descripcion("Memoria RAM de alta velocidad para gaming")
                .precio(149.99)
                .build();

        Producto productoGuardado = Producto.builder()
                .id(1L)
                .nombre("Memoria RAM DDR5 32GB")
                .descripcion("Memoria RAM de alta velocidad para gaming")
                .precio(149.99)
                .build();

        when(repository.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto resultado = productoService.agregarProducto(productoParaGuardar);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Memoria RAM DDR5 32GB", resultado.getNombre());
        verify(repository, times(1)).save(productoParaGuardar);
    }

    @Test
    void testEliminarProductoCuandoExiste() {
        Long idExistente = 10L;

        when(repository.existsById(idExistente)).thenReturn(true);


        productoService.eliminarProducto(idExistente);
        verify(repository, times(1)).existsById(idExistente);
        verify(repository, times(1)).deleteById(idExistente);
    }

    @Test
    void testEliminarProductoCuandoNoExiste() {

        Long idNoExistente = 99L;
        when(repository.existsById(idNoExistente)).thenReturn(false);

        productoService.eliminarProducto(idNoExistente);

        verify(repository, times(1)).existsById(idNoExistente);
        verify(repository, never()).deleteById(idNoExistente);
    }
}