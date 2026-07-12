package com.example.ventas.service;


import com.example.ventas.client.CarritoClient;
import com.example.ventas.client.InventarioClient;
import com.example.ventas.client.ProductoClient;
import com.example.ventas.client.UserClient;
import com.example.ventas.model.CarritoDTO;
import com.example.ventas.model.DetalleVenta;

import com.example.ventas.model.Venta;
import com.example.ventas.respository.VentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
@RequiredArgsConstructor
public class VentaService {

    @Autowired
    private final VentaRepository repository;
    @Autowired
    private final CarritoClient carritoClient;
    @Autowired
    private final ProductoClient productoClient;
    @Autowired
    private final UserClient userClient;
    @Autowired
    private final InventarioClient inventarioClient;

    @Transactional
    public Mono<Venta> procesarVenta(Long idUsuario){
        return userClient.obtenerUsuario(idUsuario)
                .flatMap(usuario -> carritoClient.obtenerCarritoPorUsuario(idUsuario))
                .flatMap((List<CarritoDTO> items) -> {
                    if (items == null || items.isEmpty()) {
                        return Mono.error(new RuntimeException("El carrito esta vacio o no existe, no se puede procesar la venta"));
                    }
                    return Flux.fromIterable(items)
                            .flatMap(item -> productoClient.obtenerProducto(item.getIdProducto())
                                    .map(productoDTO -> {
                                        DetalleVenta detalle = new DetalleVenta();
                                        detalle.setNombreProducto(item.getNombreProducto());
                                        detalle.setDescripcion(productoDTO.getDescripcion());
                                        detalle.setCantidad(item.getCantidad());
                                        detalle.setPrecioUnitario(productoDTO.getPrecio());
                                        return detalle;
                                    })
                            ).collectList()
                            .flatMap(detallesCompletos -> {
                                Double totalCalculado = detallesCompletos.stream()
                                        .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                                        .sum();
                                Venta venta = Venta.builder()
                                        .idUsuario(idUsuario)
                                        .total(totalCalculado) //cambio de emergencia supongo
                                        .fecha(LocalDateTime.now())
                                        .estado("PAGADO")
                                        .detalles(detallesCompletos)
                                        .build();


                                return Flux.fromIterable(items)
                                        .concatMap(item -> inventarioClient.descontarStock(item.getIdProducto(), item.getCantidad())
                                                // Si falla un descuento remoto, propagamos explícitamente el error para cancelar la transacción
                                                .onErrorResume(error -> Mono.error(new RuntimeException("Error crítico: Falló el descuento de stock para el producto ID " + item.getIdProducto() + ". Revirtiendo operación.")))
                                        )
                                        .collectList()
                                        .flatMap(ignorado -> carritoClient.vaciarCarrito(idUsuario))
                                        .flatMap(ignorado -> {
                                            return Mono.fromCallable(() -> repository.save(venta))
                                                    .subscribeOn(Schedulers.boundedElastic());
                                        })
                                        .doOnNext(venta_Procesada -> System.out.println("Venta procesada, stock descontado y carrito vaciado con exito"));
                            });
                });

    }

    public List<Venta> obtenerVentas(){
        return repository.findAll();
    }

    public void eliminarVenta(Long id){
        repository.deleteById(id);
    }

    public Optional<Venta> obtenerVentaPorId(Long id){
        return repository.findById(id);
    }
    @Transactional
    public Venta actualizarEstado(Long id, String nuevoEstado){
        return repository.findById(id)
                .map(venta -> {
                    venta.setEstado(nuevoEstado);
                    return repository.save(venta);
                })
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }


}
