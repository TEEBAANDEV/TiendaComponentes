package com.example.analitica.Controller;

import com.example.analitica.controller.ResenaController;
import com.example.analitica.model.Resena;
import com.example.analitica.servicio.ResenaService;
import com.example.analitica.client.ProductoClient;
import com.example.analitica.client.UsuarioClient;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResenaControllerTest {

    @Mock
    private ResenaService resenaService;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ResenaController controller;

    @Test
    @DisplayName("Given un ID existente, When se solicita por URL, Then retorna la respuesta con estatus 200 usando block")
    void deberiaObtenerResenaPorId() {
        // Given
        Long id = 1L;
        Resena resena = new Resena();
        resena.setId(id);

        when(resenaService.obtenerPorId(id)).thenReturn(Optional.of(resena));

        // When
        ResponseEntity<Resena> response = controller.obtenerResenaPorId(id).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Given un ID de producto, When se solicita el promedio, Then retorna el mapa con las estadisticas y estatus 200")
    void deberiaObtenerPromedioDeCalificaciones() {
        // Given
        Long productoId = 102L;
        Map<String, Object> mockMapa = Map.of(
                "productoId", productoId,
                "promedio_estrellas", 4.5,
                "total_votos", 10
        );

        when(resenaService.obtenerSoloPromedio(productoId)).thenReturn(Mono.just(mockMapa));

        // When
        ResponseEntity<Map<String, Object>> response = controller.getPromedio(productoId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4.5, response.getBody().get("promedio_estrellas"));
    }

    @Test
    @DisplayName("Given que existen reseñas, When se listan todas, Then inyecta HATEOAS y retorna estatus 200")
    void deberiaListarComentariosConHateoas() {
        // Given
        Resena r1 = new Resena();
        r1.setId(1L);
        when(resenaService.obtenerComentarios()).thenReturn(List.of(r1));

        // When
        ResponseEntity<List<Resena>> response = controller.obtenerComentarios();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}