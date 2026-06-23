package com.example.recibo.controller;

import com.example.recibo.controller.ReciboController;
import com.example.recibo.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static org.mockito.Mockito.when;

@WebFluxTest(ReciboController.class)
class ReciboControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.example.recibo.client.ReciboClient reciboClient;

    @MockitoBean
    private com.example.recibo.client.VentaClient ventaClient;

    @MockitoBean
    private com.example.recibo.service.ReciboService service;


    @Test
    void deberiaListarElementos() {
        when(service.listar())
                .thenReturn(Collections.emptyList());

        webTestClient.get()
                .uri("/api/v1/recibo")
                .exchange()
                .expectStatus().isOk();
    }
}
