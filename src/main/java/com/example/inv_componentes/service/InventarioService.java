package com.example.inv_componentes.service;


import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.repository.InventarioRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

     @Autowired
     private InventarioRespository repository;

     public Inventario save(Inventario inventario){
         return repository.save(inventario);
     }

     public List<Inventario> listar(){
         return repository.findAll();
     }

     public void descontarStock(Long idProducto, Integer cantidadComprada){
         repository.findByIdProducto(idProducto).ifPresent(inventario -> {
             int nuevoStock = inventario.getCantidad() - cantidadComprada;
             inventario.setCantidad(Math.max(nuevoStock,0));
             repository.save(inventario);
         });
     }

     public Optional<Inventario> buscarPorIdproducto(Long idProducto){
         return repository.findByIdProducto(idProducto);
     }
}
