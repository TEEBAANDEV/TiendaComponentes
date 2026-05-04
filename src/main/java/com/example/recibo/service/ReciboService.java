package com.example.recibo.service;

import com.example.recibo.model.Recibo;
import com.example.recibo.repository.ReciboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReciboService {

    @Autowired
    private ReciboRepository repository;

    public Recibo save(Recibo Recibo) {
        return  repository.save(Recibo);
    }
    public List<Recibo> listar(){
        return repository.findAll();
    }
}
