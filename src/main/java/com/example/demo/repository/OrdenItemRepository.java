package com.example.demo.repository;

import com.example.demo.model.entity.OrdenItem;
import com.example.demo.model.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {

    List<OrdenItem> findByOrden(Orden orden);
}
