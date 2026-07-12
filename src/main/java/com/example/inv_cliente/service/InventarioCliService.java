package com.example.inv_cliente.service;


import com.example.inv_cliente.client.InventarioClient;
import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.repository.InventarioCliRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioCliService {

     @Autowired
     private final InventarioCliRespository repository;
     private final InventarioClient inventarioClient;

    public Mono<Inventario_cliente> agregarAlCarrito(Inventario_cliente inventarioCliente){
        return inventarioClient.obtenerStock(inventarioCliente.getIdProducto())
                .flatMap(dto -> {

                    if (dto.getCantidad() < inventarioCliente.getCantidad()) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "No se puede agregar al carrito. Stock insuficiente. Disponible: "
                                        + dto.getCantidad() + ", Solicitado: " + inventarioCliente.getCantidad()
                        ));
                    }

                    return Mono.fromCallable(() -> repository.save(inventarioCliente))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

     public List<Inventario_cliente> obtenerCarritoPorUsuario(Long idUsuario){
         return repository.findByIdUsuario(idUsuario);
     }

     public void eliminarDelCarrito(Long id){
         repository.deleteById(id);
     }

     public void vaciarCarritoPorUsuario(Long idUsuario){
         List<Inventario_cliente> items = repository.findByIdUsuario(idUsuario);
         repository.deleteAll(items);
     }

     public List<Inventario_cliente> listar(){
         return repository.findAll();
     }
}
