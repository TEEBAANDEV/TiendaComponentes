package com.example.recibo.service;

import com.example.recibo.model.Recibo;
import com.example.recibo.repository.ReciboRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReciboApplicationTests {

    @Mock
    private ReciboRepository repository;

    @InjectMocks
    private ReciboService reciboService;

    @Test
    void testObtenerPorIdNoEncontrado() {
        Long idInexistente = 99L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Recibo> resultado = reciboService.obtenerPorId(idInexistente);

        assertTrue(resultado.isEmpty(), "El resultado debería estar vacío si el ID no existe");
    }
}