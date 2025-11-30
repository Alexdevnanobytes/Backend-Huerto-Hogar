package com.example.demo.service;

import com.example.demo.model.entity.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {

    List<Rol> findAll();

    Optional<Rol> findById(Long id);

    Rol save(Rol rol);

    void deleteById(Long id);
}
