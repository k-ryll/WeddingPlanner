package com.example.wedding.repository;

import com.example.wedding.model.DinnerTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<DinnerTable, Integer> {
    List<DinnerTable> findByProject_Id(Integer projectId);
} 