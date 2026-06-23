package com.example.inv_componentes;

import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.repository.InventarioRespository;
import com.example.inv_componentes.service.InventarioService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class  InvComponentesApplicationTests {

	@Mock
	private InventarioRespository repository;

	@InjectMocks
	private InventarioService service;

	@Test
	void deberiaRetornarListaVaciaCuandoNoHayNada() {
		Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

		List<Inventario> resultado = service.listar();

		assertNotNull(resultado);
		assertEquals(0, resultado.size());
		verify(repository).findAll();
	}

	@Test
	void deberiaRetornarProductoEnInventario()
	{
		Mockito.when(repository.findAll()).thenReturn(List.of(new Inventario(1L,1L,"Nombre","Description",50)));

		List<Inventario> resultado = service.listar();

		Assert.notNull(resultado);
		assertEquals(1,resultado.size());
		verify(repository).findAll();

	}}
