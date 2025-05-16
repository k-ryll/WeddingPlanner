package com.example.wedding.service;

import com.example.wedding.model.Category;
import com.example.wedding.model.Vendor;
import com.example.wedding.repository.CategoryRepository;
import com.example.wedding.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Get all vendors
     */
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
    
    /**
     * Get a vendor by ID
     */
    public Vendor getVendorById(Integer id) {
        return vendorRepository.findById(id).orElse(null);
    }
    
    /**
     * Get vendors by category
     */
    public List<Vendor> getVendorsByCategory(String categoryName) {
        Optional<Category> category = categoryRepository.findByNameIgnoreCase(categoryName);
        if (category.isPresent()) {
            return new ArrayList<>(category.get().getVendors());
        }
        return new ArrayList<>();
    }
    
    /**
     * Get all vendor categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    /**
     * Get all category names
     */
    public List<String> getAllCategoryNames() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
    
    /**
     * Get distinct vendor categories for budget category suggestions
     */
    public List<String> getDistinctVendorCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
    
    /**
     * Get or create a category by name
     */
    @Transactional
    public Category getOrCreateCategory(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Category newCategory = new Category(name);
                    return categoryRepository.save(newCategory);
                });
    }
    
    /**
     * Get vendors organized by category for the UI
     * Returns a list of VendorType objects, each containing a category name and list of vendors
     */
    public List<VendorType> getVendorsByType() {
        List<VendorType> vendorTypes = new ArrayList<>();
        Map<String, List<Vendor>> vendorsByCategory = new HashMap<>();
        
        // Group vendors by primary category (first category in their list)
        List<Vendor> allVendors = vendorRepository.findAll();
        
        // First, initialize the map with all categories
        getAllCategoryNames().forEach(category -> vendorsByCategory.put(category, new ArrayList<>()));
        
        // For each vendor, add them to each of their categories
        for (Vendor vendor : allVendors) {
            for (Category category : vendor.getCategories()) {
                String categoryName = category.getName();
                vendorsByCategory.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(vendor);
            }
        }
        
        // Create VendorType objects
        for (Map.Entry<String, List<Vendor>> entry : vendorsByCategory.entrySet()) {
            if (!entry.getValue().isEmpty()) {  // Only add categories that have vendors
                VendorType vendorType = new VendorType();
                vendorType.setName(entry.getKey());
                vendorType.setVendors(entry.getValue());
                vendorTypes.add(vendorType);
            }
        }
        
        return vendorTypes;
    }
    
    /**
     * Inner class to represent a group of vendors by type for the UI
     */
    public static class VendorType {
        private String name;
        private List<Vendor> vendors;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<Vendor> getVendors() {
            return vendors;
        }
        
        public void setVendors(List<Vendor> vendors) {
            this.vendors = vendors;
        }
    }
    
    /**
     * Save a vendor with categories
     */
    @Transactional
    public Vendor saveVendor(Vendor vendor, List<String> categoryNames) {
        // Clear existing categories to prevent orphaned entries
        vendor.getCategories().clear();
        
        // Add each category
        for (String categoryName : categoryNames) {
            Category category = getOrCreateCategory(categoryName);
            vendor.addCategory(category);
        }
        
        return vendorRepository.save(vendor);
    }
    
    /**
     * Save a vendor (legacy method for backward compatibility)
     */
    @Transactional
    public Vendor saveVendor(Vendor vendor) {
        // If we received a single category string, handle it
        if (vendor.getCategories().isEmpty() && vendor.getCategory() != null) {
            Category category = getOrCreateCategory(vendor.getCategory());
            vendor.addCategory(category);
        }
        
        return vendorRepository.save(vendor);
    }
    
    /**
     * Delete a vendor
     */
    @Transactional
    public void deleteVendor(Integer id) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        if (vendor != null) {
            // Remove the vendor from all categories to maintain referential integrity
            Set<Category> categories = new HashSet<>(vendor.getCategories());
            for (Category category : categories) {
                vendor.removeCategory(category);
            }
            vendorRepository.delete(vendor);
        }
    }
} 