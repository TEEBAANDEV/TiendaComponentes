package com.example.ventas.service;


import com.example.ventas.model.Venta;
import com.example.ventas.respository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository repository;

    public Venta agregarVenta(Venta nuevaVenta) {
        return repository.save(nuevaVenta);
    }

    public List<Venta> obtenerVentas(){
        return repository.findAll();
    }

    public void eliminarVenta(Long id){
        repository.deleteById(id);
    }
}
