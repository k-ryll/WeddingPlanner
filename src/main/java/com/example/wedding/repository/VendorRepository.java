package com.example.wedding.repository;

import com.example.wedding.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    
    // Find vendors by category
    List<Vendor> findByCategory(String category);
    
    // Find vendors by location
    List<Vendor> findByLocation(String location);
    
    // Find vendors by price type
    List<Vendor> findByPriceType(String priceType);
    
    // Custom query to find vendors within a price range
    @Query("SELECT v FROM Vendor v WHERE v.totalPrice BETWEEN :minPrice AND :maxPrice")
    List<Vendor> findByPriceRange(Integer minPrice, Integer maxPrice);
    
    // Search vendors by name (containing the search term)
    List<Vendor> findByNameContainingIgnoreCase(String searchTerm);
    
    // Get all distinct categories
    @Query("SELECT DISTINCT v.category FROM Vendor v ORDER BY v.category")
    List<String> findAllCategories();
} 