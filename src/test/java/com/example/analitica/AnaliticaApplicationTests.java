package com.example.analitica.servicio;

import com.example.analitica.model.Resena;
import com.example.analitica.repository.Resenarepository; // Asegúrate de que coincida con tu mayúscula/minúscula
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaliticaApplicationTests {

    @Mock
    private Resenarepository repository;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void testObtenerSoloPromedioExitoso() {
        Long productoId = 4L;

        Resena resena1 = Resena.builder().productoId(productoId).calificacion(5).build();
        Resena resena2 = Resena.builder().productoId(productoId).calificacion(3).build();
        List<Resena> listaSimulada = List.of(resena1, resena2);

        when(repository.findByProductoId(productoId)).thenReturn(listaSimulada);

        Mono<Map<String, Object>> resultadoMono = resenaService.obtenerSoloPromedio(productoId);

        StepVerifier.create(resultadoMono)
                .assertNext(mapa -> {
                    assertEquals(productoId, mapa.get("productoId"));
                    assertEquals(4.0, (Double) mapa.get("promedio_estrellas"));
                    assertEquals(2, mapa.get("total_votos"));
                })
                .verifyComplete();
    }
}