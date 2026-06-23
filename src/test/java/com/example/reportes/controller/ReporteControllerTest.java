package com.example.reportes.controller;

import com.example.reportes.controller.ReporteController;
import com.example.reportes.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static org.mockito.Mockito.when;

@WebFluxTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.example.reportes.client.ReciboClient reciboClient;

    @MockitoBean
    private com.example.reportes.client.ReporteClient reporteClient;

    @MockitoBean
    private com.example.reportes.servicio.AnaliticaClientService analiticaClientService;

    @MockitoBean
    private com.example.reportes.servicio.ReporteService service;


    @Test
    void deberiaListarElementos() {
        when(service.findAll())
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/reportes")
                .exchange()
                .expectStatus().isOk();
    }
}
