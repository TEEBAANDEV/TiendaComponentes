package com.example.inv_cliente.service;
import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.repository.ListaDeseosRespository;
import com.example.inv_cliente.service.ListaDeseosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaDeseosServiceTest {

    @Mock
    private ListaDeseosRespository repository;
    @Mock
    private ProductoClient productoClient;
    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ListaDeseosService service;

    @Test
    @DisplayName("Given un item nuevo, When se registra, Then consulta clientes remotos usando Mono.zip y guarda el item")
    void deberiaRegistrarItemExitosamente() {
        // Given
        ListaDeseados item = new ListaDeseados();
        item.setIdUsuario(10L);
        item.setIdProducto(102L);

        Producto productoMock = new Producto();
        productoMock.setNombre("Mouse Gamer Wireless");
        productoMock.setDescripcion("Mouse optico 16000 DPI");

        // Corregido: Instanciamos el User del paquete correspondiente
        com.example.inv_cliente.model.User usuarioMock = new com.example.inv_cliente.model.User();

        when(productoClient.obtenerProducto(102L)).thenReturn(Mono.just(productoMock));
        when(usuarioClient.obtenerUsuario(10L)).thenReturn(Mono.just(usuarioMock));
        when(repository.save(any(ListaDeseados.class))).thenReturn(item);

        // When
        ListaDeseados resultado = service.RegistrarItem(item).block();

        // Then
        assertNotNull(resultado);
        assertEquals("Mouse Gamer Wireless", resultado.getNombreProducto());
        verify(repository, times(1)).save(item);
    }

    @Test
    @DisplayName("Given un objeto de inventario cliente, When se agrega directo, Then invoca la persistencia sincrona")
    void deberiaAgregarAListaDirecto() {
        // Given
        ListaDeseados item = new ListaDeseados();
        when(repository.save(any(ListaDeseados.class))).thenReturn(item);

        // When
        ListaDeseados resultado = service.agregarALista(item);

        // Then
        assertNotNull(resultado);
        verify(repository, times(1)).save(item);
    }

    @Test
    @DisplayName("Given un ID de usuario, When se consulta su wishlist, Then retorna los deseos asociados desde la BD")
    void deberiaObtenerListaPorUsuario() {
        // Given
        Long idUsuario = 10L;
        ListaDeseados item = new ListaDeseados();
        when(repository.findByIdUsuario(idUsuario)).thenReturn(List.of(item));

        // When
        List<ListaDeseados> resultado = service.obtenerListaPorUsuario(idUsuario);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByIdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Given un ID de item existente, When se elimina de la lista, Then invoca el deleteById del repositorio")
    void deberiaEliminarDeLista() {
        // Given
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        // When
        service.eliminarDeLista(id);

        // Then
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Given un ID de usuario, When se vacia la lista, Then busca sus registros y ejecuta un deleteAll")
    void deberiaVaciarListaPorUsuario() {
        // Given
        Long idUsuario = 10L;
        ListaDeseados item = new ListaDeseados();
        List<ListaDeseados> itemsMock = List.of(item);

        when(repository.findByIdUsuario(idUsuario)).thenReturn(itemsMock);
        doNothing().when(repository).deleteAll(itemsMock);

        // When
        service.vaciarListaPorUsuario(idUsuario);

        // Then
        verify(repository, times(1)).findByIdUsuario(idUsuario);
        verify(repository, times(1)).deleteAll(itemsMock);
    }
}