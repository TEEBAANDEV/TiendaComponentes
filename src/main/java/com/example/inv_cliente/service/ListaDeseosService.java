package com.example.inv_cliente.service;


import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.repository.ListaDeseosRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaDeseosService {

     @Autowired
     private ListaDeseosRespository repository;

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
