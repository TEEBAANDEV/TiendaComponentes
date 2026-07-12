package com.example.inv_cliente.service;


import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.repository.ListaDeseosRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ListaDeseosService {


     private ListaDeseosRespository repository;
     private ProductoClient productoClient;
     private UsuarioClient usuarioClient;

     //Moví el registro de items para que el controller no lo contenta al no ser pertenenciente de alli
     public Mono<ListaDeseados> RegistrarItem(ListaDeseados item){
        return Mono.zip(
                productoClient.obtenerProducto(item.getIdProducto()),
                usuarioClient.obtenerUsuario(item.getIdUsuario())
        ).flatMap(tuple2 -> {
            Producto producto = tuple2.getT1();
            item.setNombreProducto(producto.getNombre());
            item.setDescripcionProducto(producto.getDescripcion());

            return Mono.fromCallable(() -> repository.save(item))
                    .subscribeOn(Schedulers.boundedElastic());
        });
     }

     public ListaDeseados agregarALista(ListaDeseados inventarioCliente){
         return repository.save(inventarioCliente);
     }

     public List<ListaDeseados> obtenerListaPorUsuario(Long idUsuario){
         return repository.findByIdUsuario(idUsuario);
     }

     public void eliminarDeLista(Long id){
         repository.deleteById(id);
     }

     public void vaciarListaPorUsuario(Long idUsuario){
         List<ListaDeseados> items = repository.findByIdUsuario(idUsuario);
         repository.deleteAll(items);
     }

     public List<ListaDeseados> listar(){
         return repository.findAll();
     }
}
