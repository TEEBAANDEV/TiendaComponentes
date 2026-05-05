package com.example.envios.Service;


import com.example.envios.modelo.Envio;
import com.example.envios.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository repository;

    public Envio save(Envio envio) {
        return repository.save(envio);
    }
    public List<Envio> listar(){
        return repository.findAll();
    }

    public Optional<Envio> findById(Long id){
        return repository.findById(id);
    }

}
