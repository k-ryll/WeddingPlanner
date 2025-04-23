package com.example.wedding.repository;

import com.example.wedding.model.Chair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChairRepository extends JpaRepository<Chair, Integer> {
    List<Chair> findByWeddingTableId(Integer tableId);
} 