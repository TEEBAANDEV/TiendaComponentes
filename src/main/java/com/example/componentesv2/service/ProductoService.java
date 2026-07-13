package com.example.componentesv2.service;

import com.example.componentesv2.model.Producto;
import com.example.componentesv2.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repository;

    public ProductoService(ProductoRepository repository){
        this.repository = repository;
    }

    public List<Producto> listar(){
        return repository.findAll();
    }

    public Optional<Producto> findById(Long id){
        return repository.findById(id);
    }

    public Producto agregarProducto(Producto producto){
        return repository.save(producto);
    }

    public void eliminarProducto(Long id){
        if (repository.existsById(id)){
            repository.deleteById(id);
        }
    }
}
