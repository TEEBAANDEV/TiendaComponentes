package com.example.ventas.service;


import com.example.ventas.client.CarritoClient;
import com.example.ventas.client.InventarioClient;
import com.example.ventas.client.ProductoClient;
import com.example.ventas.client.UserClient;
import com.example.ventas.model.CarritoDTO;
import com.example.ventas.model.DetalleVenta;
import com.example.ventas.model.ProductoDTO;
import com.example.ventas.model.Venta;
import com.example.ventas.respository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                                        .estado("PENDIENTE")
                                        .detalles(detallesCompletos)
                                        .build();
                                Venta ventaGuardada = repository.save(venta);
                                return Flux.fromIterable(items)
                                        .flatMap(item -> inventarioClient.descontarStock(item.getIdProducto(), item.getCantidad()))
                                        .collectList()
                                        .flatMap(ignorado -> carritoClient.vaciarCarrito(idUsuario))
                                        .thenReturn(ventaGuardada)
                                        .doOnSuccess(v -> System.out.println("Stock descontado y carrito vaciado con exito"))
                                        .onErrorResume(e -> {
                                            System.err.println("Error en segundo plano: " + e.getMessage());
                                            return Mono.just(ventaGuardada);
                                        });
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

    public Venta actualizarEstado(Long id, String nuevoEstado){
        return repository.findById(id)
                .map(venta -> {
                    venta.setEstado(nuevoEstado);
                    return repository.save(venta);
                })
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }


}
