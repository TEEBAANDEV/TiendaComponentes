package com.example.envios.controller;

import com.example.envios.Service.EnvioService;
import com.example.envios.client.ReciboClient;
import com.example.envios.client.UsuarioClient;
import com.example.envios.model.Envio;
import com.example.envios.model.ReciboDTO;
import com.example.envios.model.UsuarioDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {

    @Mock
    private EnvioService service;

    @Mock
    private ReciboClient reciboClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private EnvioController controller;

    @Test
    @DisplayName("Given un ID existente, When se consulta endpoint por ID, Then retorna 200 OK con links HATEOAS")
    void deberiaBuscarPorIdController() {
        // Given
        Long id = 1L;
        Envio envio = new Envio();
        envio.setId(id);

        when(service.findById(id)).thenReturn(Optional.of(envio));

        // When
        ResponseEntity<Envio> response = controller.buscarPorId(id);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID inválido o inexistente, When se consulta endpoint por ID, Then retorna 404 Not Found")
    void deberiaRetornar404CuandoNoExisteEnvio() {
        // Given
        Long id = 999L;
        when(service.findById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Envio> response = controller.buscarPorId(id);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Given un ID de Recibo válido, When se despacha el envío, Then obtiene datos remotos, guarda y retorna 201 Created")
    void deberiaDespacharEnvioExitosamente() {
        // Given
        Long idRecibo = 45L;

        ReciboDTO reciboMock = new ReciboDTO();
        reciboMock.setIdRecibo(idRecibo);
        reciboMock.setIdUsuario(10L);

        UsuarioDTO usuarioMock = new UsuarioDTO();
        usuarioMock.setId(10L);
        usuarioMock.setDireccion("Av. Vicuña Mackenna 4835");

        Envio envioGuardado = new Envio();
        envioGuardado.setId(1L);
        envioGuardado.setIdRecibo(idRecibo);

        when(reciboClient.obtenerRecibo(idRecibo)).thenReturn(Mono.just(reciboMock));
        when(usuarioClient.obtenerUsuario(10L)).thenReturn(Mono.just(usuarioMock));
        when(service.save(any(Envio.class))).thenReturn(Mono.just(envioGuardado));

        // When
        ResponseEntity<Envio> response = controller.despacharenvio(idRecibo);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(service, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("Given un ID de Recibo inexistente, When se intenta despachar, Then el cliente remoto es nulo y retorna 404")
    void deberiaRetornar404SiReciboNoExiste() {
        // Given
        Long idRecibo = 999L;
        when(reciboClient.obtenerRecibo(idRecibo)).thenReturn(Mono.empty());

        // When
        ResponseEntity<Envio> response = controller.despacharenvio(idRecibo);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, never()).save(any(Envio.class));
    }

    @Test
    @DisplayName("Given que ocurre una falla en los clientes feign, When se despacha, Then el catch captura la excepción y retorna 503")
    void deberiaRetornar503EnCasoDeExcepcion() {
        // Given
        Long idRecibo = 45L;
        when(reciboClient.obtenerRecibo(idRecibo)).thenThrow(new RuntimeException("Timeout connection"));

        // When
        ResponseEntity<Envio> response = controller.despacharenvio(idRecibo);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
}