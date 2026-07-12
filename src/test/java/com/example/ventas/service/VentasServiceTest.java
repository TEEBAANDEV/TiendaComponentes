package com.example.ventas.service;

import com.example.ventas.client.CarritoClient;
import com.example.ventas.client.InventarioClient;
import com.example.ventas.client.ProductoClient;
import com.example.ventas.client.UserClient;
import com.example.ventas.model.CarritoDTO;
import com.example.ventas.model.Venta;
import com.example.ventas.respository.VentaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository repository;
    @Mock
    private CarritoClient carritoClient;
    @Mock
    private ProductoClient productoClient;
    @Mock
    private UserClient userClient;
    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private VentaService service;


    @Test
    @DisplayName("Given un carrito vacio, When se procesa la venta, Then arroja RuntimeException de carrito vacio sin persistir")
    void deberiaLanzarErrorCuandoCarritoEstaVacio() {
        // Given
        Long idUsuario = 10L;

        // Corregido: Usamos tu DTO real en lugar de Object
        com.example.ventas.model.UsuarioDTO usuarioMock = new com.example.ventas.model.UsuarioDTO();

        when(userClient.obtenerUsuario(idUsuario)).thenReturn(Mono.just(usuarioMock));
        when(carritoClient.obtenerCarritoPorUsuario(idUsuario)).thenReturn(Mono.just(Collections.emptyList()));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.procesarVenta(idUsuario).block();
        });

        assertTrue(exception.getMessage().contains("El carrito esta vacio"));
        verify(repository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Given que existen ventas en la BD, When se listan, Then retorna la coleccion completa")
    void deberiaObtenerTodasLasVentas() {
        // Given
        Venta v = Venta.builder().id(101L).build();
        when(repository.findAll()).thenReturn(List.of(v));

        // When
        List<Venta> resultado = service.obtenerVentas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given un ID existente, When se solicita por ID, Then retorna el Optional con el registro")
    void deberiaObtenerVentaPorId() {
        // Given
        Long id = 101L;
        Venta v = Venta.builder().id(id).build();
        when(repository.findById(id)).thenReturn(Optional.of(v));

        // When
        Optional<Venta> resultado = service.obtenerVentaPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
    }

    @Test
    @DisplayName("Given una venta existente, When se actualiza su estado, Then altera el atributo y guarda los cambios")
    void deberiaActualizarEstadoVenta() {
        // Given
        Long id = 101L;
        Venta v = Venta.builder().id(id).estado("PAGADO").build();
        when(repository.findById(id)).thenReturn(Optional.of(v));
        when(repository.save(any(Venta.class))).thenReturn(v);

        // When
        Venta resultado = service.actualizarEstado(id, "ENTREGADO");

        // Then
        assertNotNull(resultado);
        assertEquals("ENTREGADO", v.getEstado());
        verify(repository, times(1)).save(v);
    }
}