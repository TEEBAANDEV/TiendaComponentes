package com.example.recibo.service;

import com.example.recibo.model.Recibo;
import com.example.recibo.repository.ReciboRepository;
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
class ReciboServiceTest {

    @Mock
    private ReciboRepository repository;

    @InjectMocks
    private ReciboService service;

    @Test
    @DisplayName("Given un objeto Recibo armado, When se envia a guardar, Then se almacena correctamente en la BD")
    void deberiaGuardarReciboExitosamente() {
        // Given
        Recibo recibo = new Recibo();
        recibo.setIdVenta(101L);
        recibo.setNombreProducto("1x Teclado Mecanico");
        recibo.setMontoTotal(25990.0);

        when(repository.save(any(Recibo.class))).thenReturn(recibo);

        // When
        Recibo resultado = service.save(recibo);

        // Then
        assertNotNull(resultado);
        assertEquals(101L, resultado.getIdVenta());
        assertEquals("1x Teclado Mecanico", resultado.getNombreProducto());
        verify(repository, times(1)).save(recibo);
    }

    @Test
    @DisplayName("Given registros historicos en el sistema, When se listan, Then retorna la coleccion completa")
    void deberiaListarTodosLosRecibos() {
        // Given
        Recibo r1 = new Recibo();
        when(repository.findAll()).thenReturn(List.of(r1));

        // When
        List<Recibo> resultado = service.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given un ID existente, When se busca un recibo, Then retorna un Optional con el objeto")
    void deberiaBuscarReciboPorIdExistente() {
        // Given
        Long idRecibo = 45L;
        Recibo recibo = new Recibo();
        recibo.setIdRecibo(idRecibo);

        when(repository.findById(idRecibo)).thenReturn(Optional.of(recibo));

        // When
        Optional<Recibo> resultado = service.obtenerPorId(idRecibo);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(idRecibo, resultado.get().getIdRecibo());
        verify(repository, times(1)).findById(idRecibo);
    }
}