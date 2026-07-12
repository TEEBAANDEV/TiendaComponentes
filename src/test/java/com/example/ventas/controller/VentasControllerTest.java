package com.example.ventas.controller;
import com.example.ventas.controller.VentasController;
import com.example.ventas.model.Venta;
import com.example.ventas.service.VentaService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentasControllerTest {

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentasController controller;

    @Test
    @DisplayName("Given un ID de usuario, When se genera compra de forma exitosa, Then retorna 201 Created con links HATEOAS")
    void deberiaGenerarCompraController() {
        // Given
        Long idUsuario = 10L;
        Venta ventaMock = Venta.builder().id(101L).idUsuario(idUsuario).estado("PAGADO").build();
        when(ventaService.procesarVenta(idUsuario)).thenReturn(Mono.just(ventaMock));

        // When
        ResponseEntity<Object> response = controller.generarCompra(idUsuario).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        // Convertimos el cuerpo al tipo Venta de forma segura
        Venta ventaResultado = (Venta) response.getBody();

        // Verificamos que contenga los enlaces HATEOAS inyectados por el método addLinks
        assertNotNull(ventaResultado.getLinks());
        assertFalse(ventaResultado.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given una falla en el procesamiento de venta, When se genera la compra, Then el onErrorResume responde 400 Bad Request")
    void deberiaRetornarBadRequestCuandoFallaVenta() {
        // Given
        Long idUsuario = 10L;
        when(ventaService.procesarVenta(idUsuario)).thenReturn(Mono.error(new RuntimeException("Carrito vacio")));

        // When
        ResponseEntity<Object> response = controller.generarCompra(idUsuario).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Carrito vacio", response.getBody());
    }

    @Test
    @DisplayName("Given que hay ventas en el sistema, When se listan, Then las empaqueta en un Flux con enlaces relacionales")
    void deberiaListarVentasConHateoas() {
        // Given
        Venta v1 = Venta.builder().id(1L).build();
        when(ventaService.obtenerVentas()).thenReturn(List.of(v1));

        // When
        List<Venta> resultado = controller.listarVentas().collectList().block();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertFalse(resultado.get(0).getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID de registro, When se borra, Then invoca la eliminacion en el service y retorna 204 No Content")
    void deberiaEliminarVentaController() {
        // Given
        Long id = 101L;
        doNothing().when(ventaService).eliminarVenta(id);

        // When
        ResponseEntity<Void> response = controller.eliminarVenta(id).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ventaService, times(1)).eliminarVenta(id);
    }

    @Test
    @DisplayName("Given un ID de venta valido, When se consulta por URL, Then retorna la entidad con estatus 200 OK")
    void deberiaObtenerPorIdController() {
        // Given
        Long id = 101L;
        Venta v = Venta.builder().id(id).build();
        when(ventaService.obtenerVentaPorId(id)).thenReturn(Optional.of(v));

        // When
        ResponseEntity<Venta> response = controller.obtenerVentaPorId(id).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Given un ID de venta existente, When se muta el estado vía parámetros, Then actualiza y responde 200 OK")
    void deberiaActualizarEstadoVentaController() {
        // Given
        Long id = 101L;
        Venta v = Venta.builder().id(id).estado("EN_TRANSITO").build();
        when(ventaService.actualizarEstado(id, "EN_TRANSITO")).thenReturn(v);

        // When
        ResponseEntity<Venta> response = controller.actualizarEstado(id, "EN_TRANSITO").block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("EN_TRANSITO", response.getBody().getEstado());
    }
}