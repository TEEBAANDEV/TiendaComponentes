package com.example.inv_cliente.service;


import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.repository.InventarioCliRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioCliService {

     @Autowired
     private InventarioCliRespository repository;

     public Inventario_cliente agregarAlCarrito(Inventario_cliente inventarioCliente){
         return repository.save(inventarioCliente);
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
