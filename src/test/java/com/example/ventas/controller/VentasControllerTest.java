package com.example.ventas.controller;

import com.example.ventas.controller.VentasController;
import com.example.ventas.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static org.mockito.Mockito.when;

@WebFluxTest(VentasController.class)
class VentasControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.example.ventas.client.CarritoClient carritoClient;

    @MockitoBean
    private com.example.ventas.client.InventarioClient inventarioClient;

    @MockitoBean
    private com.example.ventas.client.ProductoClient productoClient;

    @MockitoBean
    private com.example.ventas.client.UserClient userClient;

    @MockitoBean
    private com.example.ventas.service.VentaService service;


    @Test
    void deberiaListarElementos() {
        when(service.obtenerVentas())
                .thenReturn(Collections.emptyList());

        webTestClient.get()
                .uri("/api/v1/Ventas")
                .exchange()
                .expectStatus().isOk();
    }
}
