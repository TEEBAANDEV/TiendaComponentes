package com.example.inv_cliente.service;

import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.repository.ListaDeseosRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvComponentesApplicationTests {

    @Mock
    private ListaDeseosRespository repository;

    @InjectMocks
    private ListaDeseosService listaDeseosService;

    @Test
    void testVaciarListaPorUsuarioExitoso() {
        Long idUsuarioPrueba = 12L;

        ListaDeseados item1 = ListaDeseados.builder()
                .id(1L)
                .idUsuario(idUsuarioPrueba)
                .idProducto(34L)
                .nombreProducto("AMD Ryzen 5 5600X")
                .cantidad(1)
                .build();

        ListaDeseados item2 = ListaDeseados.builder()
                .id(2L)
                .idUsuario(idUsuarioPrueba)
                .idProducto(35L)
                .nombreProducto("Memoria RAM DDR5 32GB")
                .cantidad(2)
                .build();

        List<ListaDeseados> listaSimulada = List.of(item1, item2);

        when(repository.findByIdUsuario(idUsuarioPrueba)).thenReturn(listaSimulada);


        listaDeseosService.vaciarListaPorUsuario(idUsuarioPrueba);

        verify(repository, times(1)).findByIdUsuario(idUsuarioPrueba);

        verify(repository, times(1)).deleteAll(listaSimulada);
    }
}