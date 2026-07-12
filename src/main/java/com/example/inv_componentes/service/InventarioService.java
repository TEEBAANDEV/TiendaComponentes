package com.example.inv_componentes.service;


import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.repository.InventarioRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

         Inventario inventario = repository.findByIdProducto(idProducto)
                 .orElseThrow(() -> new ResponseStatusException(
                         HttpStatus.NOT_FOUND, "Producto no encontrado en el inventario"));

         if (inventario.getCantidad() < cantidadComprada) {
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "Stock insuficiente para el producto ID " + idProducto +
                             ". Disponible: " + inventario.getCantidad() + ", Solicitado: " + cantidadComprada
             );
         }

             int nuevoStock = inventario.getCantidad() - cantidadComprada;
             inventario.setCantidad(Math.max(nuevoStock,0));
             repository.save(inventario);

     }
}
