package com.example.inv_cliente.controller;

import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.service.ListaDeseosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaDeseosControllerTest {

    @Mock
    private ListaDeseosService service;

    @InjectMocks
    private ListaDeseosController controller;

    @Test
    @DisplayName("Given una lista de items de deseos, When se envia al endpoint POST, Then procesa en lote y responde con los items y links HATEOAS")
    void deberiaAgregarItemsController() {
        // Given
        ListaDeseados itemInput = new ListaDeseados();
        itemInput.setId(1L);
        itemInput.setIdUsuario(10L);

        when(service.RegistrarItem(any(ListaDeseados.class))).thenReturn(Mono.just(itemInput));

        // When
        List<ListaDeseados> resultado = controller.agregarItems(List.of(itemInput)).collectList().block();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(10L, resultado.get(0).getIdUsuario());
        assertFalse(resultado.get(0).getLinks().isEmpty()); // Valida HATEOAS (.addLinks)
    }

    @Test
    @DisplayName("Given elementos en la base de datos, When se invoca el listado global, Then emite un Flux con la coleccion enriquecida")
    void deberiaListarTodosLosDeseos() {
        // Given
        ListaDeseados item = new ListaDeseados();
        item.setId(2L);
        item.setIdUsuario(20L);

        when(service.listar()).thenReturn(List.of(item));

        // When
        List<ListaDeseados> resultado = controller.listar().collectList().block();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertFalse(resultado.get(0).getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID de usuario, When se consulta su lista, Then delega al service y mapea los datos reactivamente")
    void deberiaVerListaPorUsuarioController() {
        // Given
        Long idUsuario = 10L;
        ListaDeseados item = new ListaDeseados();
        item.setId(3L);
        item.setIdUsuario(idUsuario);

        when(service.obtenerListaPorUsuario(idUsuario)).thenReturn(List.of(item));

        // When
        List<ListaDeseados> resultado = controller.verLista(idUsuario).collectList().block();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID de item, When se solicita borrar, Then ejecuta la tarea de eliminacion asincrona")
    void deberiaEliminarItemController() {
        // Given
        Long id = 5L;
        doNothing().when(service).eliminarDeLista(id);

        // When
        Void resultado = controller.eliminarItem(id).block();

        // Then
        assertNull(resultado); // Los flujos Mono<Void> devuelven null al completarse de forma exitosa
        verify(service, times(1)).eliminarDeLista(id);
    }

    @Test
    @DisplayName("Given un ID de usuario, When se manda a vaciar su lista, Then invoca el proceso y limpia los registros")
    void deberiaVaciarListaController() {
        // Given
        Long idUsuario = 10L;
        doNothing().when(service).vaciarListaPorUsuario(idUsuario);

        // When
        Void resultado = controller.vaciarLista(idUsuario).block();

        // Then
        assertNull(resultado);
        verify(service, times(1)).vaciarListaPorUsuario(idUsuario);
    }
}