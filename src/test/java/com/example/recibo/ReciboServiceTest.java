package com.example.recibo;

import com.example.recibo.model.Recibo;
import com.example.recibo.repository.ReciboRepository;
import com.example.recibo.service.ReciboService;
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
class ReciboServiceTest {

    @Mock
    private ReciboRepository repository;

    @InjectMocks
    private ReciboService service;

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayElementos() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Recibo> resultado = service.listar();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(repository).findAll();
    }
}
