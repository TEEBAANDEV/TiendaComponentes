package com.example.analitica;

import com.example.analitica.model.Resena;
import com.example.analitica.repository.Resenarepository;
import com.example.analitica.servicio.ResenaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {


    @Mock
    private Resenarepository repository;

    @InjectMocks
    private ResenaService service;

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayElementos() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Resena> resultado = service.obtenerComentarios();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(repository).findAll();
    }
}
