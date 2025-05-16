package com.example.wedding.repository;

import com.example.wedding.model.Category;
import com.example.wedding.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    
    // Find vendors by category
    @Query("SELECT v FROM Vendor v JOIN v.categories c WHERE c.name = :categoryName")
    List<Vendor> findByCategory(@Param("categoryName") String categoryName);
    
    // Find vendors by location
    List<Vendor> findByLocation(String location);
    
    // Find vendors by price type
    List<Vendor> findByPriceType(String priceType);
    
    // Custom query to find vendors within a price range
    @Query("SELECT v FROM Vendor v WHERE v.totalPrice BETWEEN :minPrice AND :maxPrice")
    List<Vendor> findByPriceRange(Integer minPrice, Integer maxPrice);
    
    // Search vendors by name (containing the search term)
    List<Vendor> findByNameContainingIgnoreCase(String searchTerm);
    
    // Find vendors that have any of the given categories
    @Query("SELECT DISTINCT v FROM Vendor v JOIN v.categories c WHERE c IN :categories")
    List<Vendor> findByCategories(@Param("categories") List<Category> categories);
} 