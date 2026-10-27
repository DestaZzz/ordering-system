package com.example.orderingsystem.repository;

import com.example.orderingsystem.entity.ItemPesanan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPesananRepository extends JpaRepository<ItemPesanan, String> {
}
