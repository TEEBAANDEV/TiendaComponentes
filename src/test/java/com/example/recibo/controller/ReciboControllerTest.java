package com.example.recibo.controller;


import com.example.recibo.client.VentaClient;

import com.example.recibo.model.Recibo;
import com.example.recibo.model.VentaDTO;
import com.example.recibo.service.ReciboService;
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
class ReciboControllerTest {

    @Mock
    private VentaClient ventaClient;

    @Mock
    private ReciboService service;

    @InjectMocks
    private ReciboController controller;

    @Test
    @DisplayName("Given una venta valida remota, When se genera el recibo, Then concatena la glosa, persiste y retorna 201 Created")
    void deberiaCrearReciboExitosamenteDesdeVenta() {
        // Given
        Long idVenta = 101L;

        VentaDTO ventaMock = new VentaDTO();
        ventaMock.setId(idVenta);
        ventaMock.setIdUsuario(10L);
        ventaMock.setTotal(25990.0);
        ventaMock.setFecha(java.time.LocalDateTime.now());

        // Corregido: Usamos tu clase real DetalleVentaDTO
        com.example.recibo.model.DetalleVentaDTO detalle = new com.example.recibo.model.DetalleVentaDTO();
        detalle.setCantidad(1);
        detalle.setNombreProducto("Teclado Mecanico");
        detalle.setDescripcion("RGB Switch Azul");
        ventaMock.setDetalles(List.of(detalle));

        Recibo reciboGuardado = new Recibo();
        reciboGuardado.setIdRecibo(45L);

        when(ventaClient.obtenerDetalleVenta(idVenta)).thenReturn(Mono.just(ventaMock));
        when(service.save(any(Recibo.class))).thenReturn(reciboGuardado);

        // When
        ResponseEntity<Recibo> response = controller.crearRecibo(idVenta);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(45L, response.getBody().getIdRecibo());
        assertFalse(response.getBody().getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given que la venta no existe de forma remota, When se intenta generar el recibo, Then retorna 404 Not Found")
    void deberiaRetornar404SiVentaEsNula() {
        // Given
        Long idVenta = 999L;
        when(ventaClient.obtenerDetalleVenta(idVenta)).thenReturn(Mono.empty());

        // When
        ResponseEntity<Recibo> response = controller.crearRecibo(idVenta);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, never()).save(any(Recibo.class));
    }

    @Test
    @DisplayName("Given una falla de conexion con el cliente de ventas, When se genera el recibo, Then el catch maneja la excepcion y retorna 404")
    void deberiaRetornar404EnCasoDeExcepcionEnGlosa() {
        // Given
        Long idVenta = 101L;
        when(ventaClient.obtenerDetalleVenta(idVenta)).thenThrow(new RuntimeException("Error de red Feign"));

        // When
        ResponseEntity<Recibo> response = controller.crearRecibo(idVenta);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Given un ID de recibo existente, When se consulta por URL, Then devuelve 200 OK y metadatos HATEOAS")
    void deberiaObtenerPorIdController() {
        // Given
        Long idRecibo = 45L;
        Recibo recibo = new Recibo();
        recibo.setIdRecibo(idRecibo);

        when(service.obtenerPorId(idRecibo)).thenReturn(Optional.of(recibo));

        // When
        ResponseEntity<Optional<Recibo>> response = controller.obtenerPorId(idRecibo);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertFalse(response.getBody().get().getLinks().isEmpty());
    }
}