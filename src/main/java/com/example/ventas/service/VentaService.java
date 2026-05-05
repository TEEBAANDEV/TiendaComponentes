package com.example.ventas.service;


import com.example.ventas.client.CarritoClient;
import com.example.ventas.client.ProductoClient;
import com.example.ventas.model.CarritoDTO;
import com.example.ventas.model.Venta;
import com.example.ventas.respository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    @Autowired
    private final VentaRepository repository;
    @Autowired
    private final CarritoClient carritoClient;
    @Autowired
    private final ProductoClient productoClient;

    public Mono<Venta> procesarVenta(Long idUsuario){
        return carritoClient.obtenerCarritoPorUsuario(idUsuario)
                .flatMap((List<CarritoDTO> items) ->{
                    if(items == null || items.isEmpty()){
                        return Mono.error(new RuntimeException("El carrito esta vacio, no se puede procesar la venta"));
                    }
                    return Flux.fromIterable(items)
                            .flatMap(item -> productoClient.obtenerProducto(item.getIdProducto())
                                    .map(productoDTO -> productoDTO.getPrecio() * item.getCantidad())
                            )
                            .reduce(0.0, Double::sum)
                            .map(totalCalculado -> {
                               Venta nuevaVenta = Venta.builder()
                                       .idUsuario(idUsuario)
                                       .total(totalCalculado)
                                       .fecha(LocalDateTime.now())
                                       .estado("PAGADA")
                                       .build();
                               return repository.save(nuevaVenta);
                            });
                });
    }

    public List<Venta> obtenerVentas(){
        return repository.findAll();
    }

    public void eliminarVenta(Long id){
        repository.deleteById(id);
    }


}
