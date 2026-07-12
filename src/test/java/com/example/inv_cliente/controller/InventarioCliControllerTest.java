package com.example.inv_cliente.controller;

import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.model.User;
import com.example.inv_cliente.security.jwt.JwtService;
import com.example.inv_cliente.service.InventarioCliService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebFluxTest(InventarioCliController.class)
class InventarioCliControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProductoClient productoClient;

    @MockitoBean
    private UsuarioClient usuarioClient;

    @MockitoBean
    private InventarioCliService service;

    @DisplayName("Deberia listar los elementos disponibles")
    @Test
    void deberiaListarElementos() {
        // Given
        when(service.listar()).thenReturn(Collections.emptyList());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/carrito")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Inventario_cliente.class)
                .hasSize(0);
    }

    @DisplayName("Deberia agregar los items al carrito")
    @Test
    void deberiaAgregarItemsAlCarrito() {
        // Given
        Inventario_cliente item = new Inventario_cliente();
        item.setIdUsuario(1L);
        item.setIdProducto(10L);
        item.setCantidad(2);

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripcion Test");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // When & Then
        when(productoClient.obtenerProducto(10L)).thenReturn(Mono.just(producto));
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(Mono.just(user));
        when(service.agregarAlCarrito(any(Inventario_cliente.class))).thenReturn(Mono.just(item));
        webTestClient.post()
                .uri("/api/v1/carrito/lote")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Collections.singletonList(item))
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(Inventario_cliente.class)
                .hasSize(1);
    }
    @DisplayName("Deberia mostrar el carrito de un usuario")

    @Test
    void deberiaVerCarritoDeUsuario() {
        // Given
        Long idUsuario = 1L;
        Inventario_cliente item = new Inventario_cliente();
        item.setId(1L);
        item.setIdUsuario(idUsuario);
        item.setIdProducto(10L);
        item.setCantidad(2);

        // When & Then
        when(service.obtenerCarritoPorUsuario(idUsuario)).thenReturn(Collections.singletonList(item));

        webTestClient.get()
                .uri("/api/v1/carrito/usuario/" + idUsuario)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Inventario_cliente.class)
                .hasSize(1);
    }

    @DisplayName("Elimina un item del carrito")
    @Test
    void deberiaEliminarItemDelCarrito() {
        // Given
        Long idItem = 5L;
        doNothing().when(service).eliminarDelCarrito(idItem);

        // When & Then
        webTestClient.delete()
                .uri("/api/v1/carrito/" + idItem)
                .exchange()
                .expectStatus().isNoContent();
    }

    @DisplayName("Debería vaciar el carrito del usario al completo")
    @Test
    void deberiaVaciarCarritoDeUsuario() {
        // Given
        Long idUsuario = 1L;
        doNothing().when(service).vaciarCarritoPorUsuario(idUsuario);

        // When & Then
        webTestClient.delete()
                .uri("/api/v1/carrito/usuario/" + idUsuario)
                .exchange()
                .expectStatus().isNoContent();
    }
}

