package com.example.analitica.Service;

import com.example.analitica.model.Resena;
import com.example.analitica.repository.Resenarepository;
import com.example.analitica.servicio.ResenaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private Resenarepository repository;

    @InjectMocks
    private ResenaService service;

    @Test
    @DisplayName("Given que un producto tiene reseñas, When se solicita el promedio, Then calcula la media y total de votos usando block")
    void deberiaCalcularPromedioExitosamente() {
        // Given
        Long productoId = 102L;
        Resena r1 = new Resena(); r1.setCalificacion(5);
        Resena r2 = new Resena(); r2.setCalificacion(3);
        List<Resena> listaMock = List.of(r1, r2);

        when(repository.findByProductoId(productoId)).thenReturn(listaMock);

        // When
        Map<String, Object> resultado = service.obtenerSoloPromedio(productoId).block();

        // Then
        assertNotNull(resultado);
        assertEquals(productoId, resultado.get("productoId"));
        assertEquals(4.0, resultado.get("promedio_estrellas"));
        assertEquals(2, resultado.get("total_votos"));
        verify(repository, times(1)).findByProductoId(productoId);
    }

    @Test
    @DisplayName("Given un producto sin reseñas, When se solicita el promedio, Then retorna promedio 0.0 y 0 votos usando block")
    void deberiaRetornarPromedioCeroCuandoNoHayResenas() {
        // Given
        Long productoId = 999L;
        when(repository.findByProductoId(productoId)).thenReturn(Collections.emptyList());

        // When
        Map<String, Object> resultado = service.obtenerSoloPromedio(productoId).block();

        // Then
        assertNotNull(resultado);
        assertEquals(0.0, resultado.get("promedio_estrellas"));
        assertEquals(0, resultado.get("total_votos"));
    }

    @Test
    @DisplayName("Given que existen comentarios, When se listan, Then retorna la lista completa del repositorio")
    void deberiaObtenerTodosLosComentarios() {
        // Given
        Resena resena = new Resena();
        when(repository.findAll()).thenReturn(List.of(resena));

        // When
        List<Resena> resultado = service.obtenerComentarios();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given una nueva reseña, When se guarda, Then el repositorio la persiste correctamente")
    void deberiaCrearResena() {
        // Given
        Resena resena = new Resena();
        resena.setComentario("Excelente");
        when(repository.save(any(Resena.class))).thenReturn(resena);

        // When
        Resena resultado = service.crearResena(resena);

        // Then
        assertNotNull(resultado);
        assertEquals("Excelente", resultado.getComentario());
        verify(repository, times(1)).save(resena);
    }

    @Test
    @DisplayName("Given un ID válido, When se busca una reseña, Then retorna el Optional con el dato")
    void deberiaObtenerPorId() {
        // Given
        Long id = 1L;
        Resena resena = new Resena();
        resena.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(resena));

        // When
        Optional<Resena> resultado = service.obtenerPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
    }
}