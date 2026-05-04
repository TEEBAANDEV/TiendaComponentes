package com.example.inv_componentes.service;


import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.repository.InventarioRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
