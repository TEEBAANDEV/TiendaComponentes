package com.example.reportes;

import com.example.reportes.model.Reporte;
import com.example.reportes.repository.ReporteRepository;
import com.example.reportes.servicio.ReporteService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository repository;

    @InjectMocks
    private ReporteService service;

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayElementos() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        Flux<Reporte> resultado = service.findAll();

        assertNotNull(resultado);
        List<Reporte> list = resultado.collectList().block();
        assertNotNull(list);
        assertEquals(0, list.size());
        verify(repository).findAll();
    }
}
