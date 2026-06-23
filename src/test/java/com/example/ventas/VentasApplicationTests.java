package com.example.ventas;

import com.example.ventas.model.Venta;
import com.example.ventas.respository.VentaRepository;
import com.example.ventas.service.VentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentasApplicationTests {

	@Mock
	private VentaRepository repository;

	@InjectMocks
	private VentaService service;

	@Test
	void deberiaRetornarListaVaciaCuandoNoHayNada() {
		when(repository.findAll()).thenReturn(Collections.emptyList());

		List<Venta> resultado = service.obtenerVentas();

		assertNotNull(resultado);
		assertEquals(0, resultado.size());
		verify(repository).findAll();
	}
	@Test
	@DisplayName("Debería retornar una venta opcional cuando se busca por un ID existente")
	void deberiaObtenerVentaPorIdCuandoExiste() {
		Long idVenta = 1L;
		Venta ventaEsperada = Venta.builder().id(idVenta).total(25000.0).estado("PAGADO").build();
		when(repository.findById(idVenta)).thenReturn(Optional.of(ventaEsperada));

		Optional<Venta> resultado = service.obtenerVentaPorId(idVenta);

		assertTrue(resultado.isPresent(), "La venta debería ser encontrada");
		assertEquals(25000.0, resultado.get().getTotal());
		verify(repository, times(1)).findById(idVenta);
	}

	@Test
	@DisplayName("Debería actualizar el estado de la venta correctamente")
	void deberiaActualizarEstadoExitosamente() {
		Long idVenta = 10L;
		Venta ventaInicial = Venta.builder().id(idVenta).estado("PENDIENTE").build();
		Venta ventaModificada = Venta.builder().id(idVenta).estado("ENTREGADO").build();

		when(repository.findById(idVenta)).thenReturn(Optional.of(ventaInicial));
		when(repository.save(any(Venta.class))).thenReturn(ventaModificada);

		Venta resultado = service.actualizarEstado(idVenta, "ENTREGADO");

		assertNotNull(resultado);
		assertEquals("ENTREGADO", resultado.getEstado());
		verify(repository, times(1)).findById(idVenta);
		verify(repository, times(1)).save(any(Venta.class));
	}


	@Test
	@DisplayName("Debería lanzar RuntimeException al intentar actualizar una venta que no existe")
	void deberiaLanzarExcepcionAlActualizarVentaInexistente() {

		Long idInexistente = 99L;
		when(repository.findById(idInexistente)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> {
			service.actualizarEstado(idInexistente, "CANCELADO");
		}, "Se esperaba RuntimeException debido a que el ID no se encuentra registrado");

		verify(repository, times(1)).findById(idInexistente);
		verify(repository, never()).save(any(Venta.class));
	}

}

