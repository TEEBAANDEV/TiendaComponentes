package com.example.pago.service;

import com.example.pago.client.VentaClient;
import com.example.pago.model.Pago;
import com.example.pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {
    @Autowired
    private final PagoRepository repository;

    private final VentaClient ventaClient;

    public List<Pago> listar(){
        return repository.findAll();
    }
    public Optional<Pago> findById(Long id){
        return repository.findById(id);
    }
    public Pago procesarPago(Pago pago){
        pago.setFechaPago(LocalDateTime.now());
        if(pago.getMontoPagado() != null && pago.getMontoPagado() > 0){
            pago.setEstadoPago("APROBADO");
        }else{
            pago.setEstadoPago("RECHAZADO");
        }
        Pago pagoGuardado = repository.save(pago);

        if("APROBADO".equalsIgnoreCase(pagoGuardado.getEstadoPago())){
            actualizarEstadoVenta(pagoGuardado.getIdPago(),"PAGADA");
        }
        return pagoGuardado;
    }

    public void actualizarEstadoVenta(Long idVenta, String nuevoEstado){
        ventaClient.obtenerDetalleVenta(idVenta)
                .subscribe(venta -> {
                    System.out.println("Venta " + idVenta + " encontrada. Procediendo a marcar como " + nuevoEstado);
                });
    }

    public void eliminarPago(Long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }
    }
}
