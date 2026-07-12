package com.example.reportes.servicio;


import com.example.reportes.model.Reporte;
import com.example.reportes.repository.ReporteRepository;
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
class ReporteServiceTest {

    @Mock
    private ReporteRepository repository;

    @InjectMocks
    private ReporteService service;

    @Test
    @DisplayName("Given que existen reportes guardados, When se buscan todos, Then los empaqueta en un Flux y retorna la lista")
    void deberiaListarTodosLosReportes() {
        // Given
        Reporte rep = new Reporte();
        rep.setId(1L);
        rep.setNombre("Reporte Anual");
        when(repository.findAll()).thenReturn(List.of(rep));

        // When
        List<Reporte> resultado = service.findAll().collectList().block();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Reporte Anual", resultado.get(0).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given un ID valido, When se consulta por ID, Then retorna el Mono con el reporte correspondiente")
    void deberiaBuscarReportePorId() {
        // Given
        Long id = 1L;
        Reporte rep = new Reporte();
        rep.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(rep));

        // When
        Reporte resultado = service.findById(id).block();

        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given un nuevo reporte, When se guarda, Then lo persiste en la BD y retorna sus propiedades")
    void deberiaGuardarUnReporte() {
        // Given
        Reporte rep = new Reporte();
        rep.setNombre("Reporte de Auditoria");
        when(repository.save(any(Reporte.class))).thenReturn(rep);

        // When
        Reporte resultado = service.save(rep).block();

        // Then
        assertNotNull(resultado);
        assertEquals("Reporte de Auditoria", resultado.getNombre());
        verify(repository, times(1)).save(rep);
    }

    @Test
    @DisplayName("Given un ID de reporte, When se solicita eliminar, Then ejecuta el metodo de borrado de forma asincrona")
    void deberiaEliminarReportePorId() {
        // Given
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        // When
        service.deleteById(id).block();

        // Then
        verify(repository, times(1)).deleteById(id);
    }
}