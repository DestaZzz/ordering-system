package com.example.orderingsystem.repository;

import com.example.orderingsystem.entity.Pesanan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PesananRepository extends JpaRepository<Pesanan, String> {
}
