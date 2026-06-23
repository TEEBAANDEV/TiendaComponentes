package com.example.inv_cliente;

import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.controller.ListaDeseosController;
import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.model.User;
import com.example.inv_cliente.security.jwt.JwtService;
import com.example.inv_cliente.service.ListaDeseosService;
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


@WebFluxTest(ListaDeseosController.class)
class ListaDeseosApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProductoClient productoClient;

    @MockitoBean
    private UsuarioClient usuarioClient;

    @MockitoBean
    private ListaDeseosService service;

    @DisplayName("Deberia listar los elementos disponibles")
    @Test
    void deberiaListarElementos() {
        when(service.listar()).thenReturn(Collections.emptyList());
        webTestClient.get()
                .uri("/api/v1/wishlist")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ListaDeseados.class)
                .hasSize(0);
    }

    @DisplayName("Deberia agregar los items a la lista de deseos")
    @Test
    void deberiaAgregarItemsAlWishlist() {
        ListaDeseados item = new ListaDeseados();
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

        when(productoClient.obtenerProducto(10L)).thenReturn(Mono.just(producto));
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(Mono.just(user));
        when(service.agregarALista(any(ListaDeseados.class))).thenReturn(item);

        webTestClient.post()
                .uri("/api/v1/wishlist/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Collections.singletonList(item))
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(ListaDeseados.class)
                .hasSize(1);
    }

    @DisplayName("Deberia mostrar la lista de deseos de un usuario")
    @Test
    void deberiaMostrarWishlistDeUsuario() {
        Long idUsuario = 1L;
        ListaDeseados item = new ListaDeseados();
        item.setId(1L);
        item.setIdUsuario(idUsuario);
        item.setIdProducto(10L);
        item.setCantidad(2);

        when(service.obtenerListaPorUsuario(idUsuario)).thenReturn(Collections.singletonList(item));

        webTestClient.get()
                .uri("/api/v1/wishlist/usuario/" + idUsuario)
                .header("Host", "localhost")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ListaDeseados.class)
                .hasSize(1);
    }

    @DisplayName("Elimina un item de la lista de deseos")
    @Test
    void deberiaEliminarItemDeLaWishlist() {
        Long idItem = 5L;
        doNothing().when(service).eliminarDeLista(idItem);

        webTestClient.delete()
                .uri("/api/v1/wishlist/" + idItem)
                .header("Host", "localhost")
                .exchange()
                .expectStatus().isNoContent();
    }

    @DisplayName("Debería vaciar la lista de deseos del usuario")
    @Test
    void deberiaVaciarWishlistDeUsuario() {
        Long idUsuario = 1L;
        doNothing().when(service).vaciarListaPorUsuario(idUsuario);

        webTestClient.delete()
                .uri("/api/v1/wishlist/usuario/" + idUsuario)
                .header("Host", "localhost")
                .exchange()
                .expectStatus().isNoContent();
    }
}