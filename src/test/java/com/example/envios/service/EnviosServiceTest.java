package com.example.envios.service;

import com.example.envios.Service.EnvioService;
import com.example.envios.model.Envio;
import com.example.envios.repository.EnvioRepository;
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
class EnvioServiceTest {

    @Mock
    private EnvioRepository repository;

    @InjectMocks
    private EnvioService service;

    @Test
    @DisplayName("Given un nuevo objeto de Envío, When se manda a guardar, Then se persiste reactivamente y se obtiene el resultado")
    void deberiaGuardarEnvioExitosamente() {
        // Given
        Envio envio = new Envio();
        envio.setIdRecibo(45L);
        envio.setEmpresaTransporte("Starken");

        when(repository.save(any(Envio.class))).thenReturn(envio);

        // When
        Envio resultado = service.save(envio).block();

        // Then
        assertNotNull(resultado);
        assertEquals("Starken", resultado.getEmpresaTransporte());
        assertEquals(45L, resultado.getIdRecibo());
        verify(repository, times(1)).save(envio);
    }

    @Test
    @DisplayName("Given que existen envíos registrados, When se listan todos, Then retorna la lista completa desde la BD")
    void deberiaListarTodosLosEnvios() {
        // Given
        Envio e1 = new Envio();
        when(repository.findAll()).thenReturn(List.of(e1));

        // When
        List<Envio> resultado = service.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given un ID de envío existente, When se busca por ID, Then retorna un Optional con el envío usando bloqueos elásticos")
    void deberiaBuscarPorIdExistente() {
        // Given
        Long id = 1L;
        Envio envio = new Envio();
        envio.setId(id);
        envio.setEstadoEnvio("PROCESANDO_LOGISTICA");

        when(repository.findById(id)).thenReturn(Optional.of(envio));

        // When
        Optional<Envio> resultado = service.findById(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("PROCESANDO_LOGISTICA", resultado.get().getEstadoEnvio());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given un ID de envío inexistente, When se busca por ID, Then retorna un Optional vacío")
    void deberiaRetornarVacioCuandoIdNoExiste() {
        // Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Envio> resultado = service.findById(id);

        // Then
        assertFalse(resultado.isPresent());
        verify(repository, times(1)).findById(id);
    }
}