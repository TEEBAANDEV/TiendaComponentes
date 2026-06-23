package com.example.envios;

import com.example.envios.model.Envio;
import com.example.envios.repository.EnvioRepository;
import com.example.envios.Service.EnvioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnviosApplicationTests {

	@Mock
	private EnvioRepository repository;

	@InjectMocks
	private EnvioService service;

	@Test
	void deberiaRetornarListaVaciaCuandoEstaVacia() {
		Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

		Flux<Envio> resultado = service.listar();

		assertNotNull(resultado);
		List<Envio> list = resultado.collectList().block();
		assertNotNull(list);
		assertEquals(0, list.size());
		verify(repository).findAll();
	}
	@Test
	@DisplayName("Debería retornar los envíos almacenados cuando existen elementos en el sistema")
	void deberiaRetornarListaConEnviosCuandoExistenEnvios() {

		Envio envioMock = new Envio();
		envioMock.setId(1L);
		envioMock.setDireccionDestino("Av. Vitacura 1234, Santiago");
		envioMock.setEstadoEnvio("EN_TRANSITO");

		List<Envio> listaSimulada = List.of(envioMock);
		Mockito.when(repository.findAll()).thenReturn(listaSimulada);

		Flux<Envio> resultado = service.listar();

		assertNotNull(resultado);
		List<Envio> listaFinal = resultado.collectList().block();

		assertNotNull(listaFinal);
		assertEquals(1, listaFinal.size(), "La lista debería contener exactamente un envío");
		assertEquals("EN_TRANSITO", listaFinal.get(0).getEstadoEnvio());
		assertEquals("Av. Vitacura 1234, Santiago", listaFinal.get(0).getDireccionDestino());
		verify(repository, Mockito.times(1)).findAll();
	}

}
