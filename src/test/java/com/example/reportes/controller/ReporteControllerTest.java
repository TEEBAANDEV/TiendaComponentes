package com.example.reportes.controller;

import com.example.reportes.client.ReciboClient;
import com.example.reportes.controller.ReporteController;
import com.example.reportes.model.ReciboDTO; // Reemplaza por la ruta o nombre exacto de tu DTO de Recibo si varía
import com.example.reportes.model.Reporte;
import com.example.reportes.servicio.ReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteService service;

    @Mock
    private ReciboClient reciboClient;

    @InjectMocks
    private ReporteController controller;

    @Test
    @DisplayName("Given una lista de reportes en el servicio, When se invoca el GET global, Then inyecta links HATEOAS a cada uno")
    void deberiaListarReportesConHateoas() {
        // Given
        Reporte r1 = new Reporte();
        r1.setId(1L);
        when(service.findAll()).thenReturn(Flux.just(r1));

        // When
        List<Reporte> resultado = controller.listar().collectList().block();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertFalse(resultado.get(0).getLinks().isEmpty()); // Valida HATEOAS
    }

    @Test
    @DisplayName("Given un ID existente, When se busca por URL, Then retorna el reporte con su respectivo Self Link")
    void deberiaObtenerUnReportePorId() {
        // Given
        Long id = 1L;
        Reporte r1 = new Reporte();
        r1.setId(id);
        when(service.findById(id)).thenReturn(Mono.just(r1));

        // When
        Reporte resultado = controller.obtener(id).block();

        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertFalse(resultado.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID de recibo remoto valido, When se solicita generar, Then consume el cliente Feign, construye el reporte y retorna 201 Created")
    void deberiaGenerarYCrearReporteExitosamente() {
        // Given
        Long idRecibo = 45L;

        // Simulamos tu DTO de entrada remota
        ReciboDTO reciboMock = new ReciboDTO();
        reciboMock.setIdRecibo(idRecibo);
        reciboMock.setIdUsuario(10L);
        reciboMock.setNombreProducto("Teclado Mecanico");

        Reporte reporteGuardado = new Reporte();
        reporteGuardado.setId(1L);

        when(reciboClient.obtenerRecibo(idRecibo)).thenReturn(Mono.just(reciboMock));
        when(service.save(any(Reporte.class))).thenReturn(Mono.just(reporteGuardado));

        // When
        ResponseEntity<Reporte> response = controller.crearReporte(idRecibo).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Given un error en el cliente remoto, When se genera el reporte, Then entra al onErrorResume y retorna 500 Internal Server Error")
    void deberiaRetornar500CuandoFallaElClienteRemoto() {
        // Given
        Long idRecibo = 45L;
        when(reciboClient.obtenerRecibo(idRecibo)).thenReturn(Mono.error(new RuntimeException("WebClient Fails")));

        // When
        ResponseEntity<Reporte> response = controller.crearReporte(idRecibo).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(service, never()).save(any(Reporte.class));
    }
}