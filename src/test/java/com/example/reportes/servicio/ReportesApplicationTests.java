package com.example.reportes.servicio;

import com.example.reportes.model.Reporte;
import com.example.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportesApplicationTests {

    @Mock
    private ReporteRepository repository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void testFindAllExitoso() {
        Reporte reporte1 = Reporte.builder()
                .id(1L)
                .nombre("Reporte de Venta - Recibo #100")
                .tipoReporte("Venta_Cliente")
                .estado("ACTIVO")
                .build();

        Reporte reporte2 = Reporte.builder()
                .id(2L)
                .nombre("Reporte de Venta - Recibo #101")
                .tipoReporte("Venta_Cliente")
                .estado("ACTIVO")
                .build();

        List<Reporte> listaSimulada = List.of(reporte1, reporte2);

        when(repository.findAll()).thenReturn(listaSimulada);

        Flux<Reporte> resultadoFlux = reporteService.findAll();

        StepVerifier.create(resultadoFlux)
                .expectNextMatches(reporte -> reporte.getId().equals(1L) &&
                        reporte.getNombre().equals("Reporte de Venta - Recibo #100"))
                .expectNextMatches(reporte -> reporte.getId().equals(2L) &&
                        reporte.getNombre().equals("Reporte de Venta - Recibo #101"))
                .verifyComplete();
    }
}