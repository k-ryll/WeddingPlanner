package com.example.wedding.service;

import com.example.wedding.model.Vendor;
import com.example.wedding.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;
    
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
    public List<Vendor> getVendorsByCategory(String category) {
        return vendorRepository.findByCategory(category);
    }
    
    /**
     * Get all vendor categories
     */
    public List<String> getAllCategories() {
        return vendorRepository.findAllCategories();
    }
    
    /**
     * Get distinct vendor categories for budget category suggestions
     */
    public List<String> getDistinctVendorCategories() {
        return vendorRepository.findAll().stream()
                .map(Vendor::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * Get vendors organized by category for the UI
     * Returns a list of VendorType objects, each containing a category name and list of vendors
     */
    public List<VendorType> getVendorsByType() {
        List<VendorType> vendorTypes = new ArrayList<>();
        Map<String, List<Vendor>> vendorsByCategory = new HashMap<>();
        
        // Group vendors by category
        List<Vendor> allVendors = vendorRepository.findAll();
        for (Vendor vendor : allVendors) {
            String category = vendor.getCategory();
            if (!vendorsByCategory.containsKey(category)) {
                vendorsByCategory.put(category, new ArrayList<>());
            }
            vendorsByCategory.get(category).add(vendor);
        }
        
        // Create VendorType objects
        for (Map.Entry<String, List<Vendor>> entry : vendorsByCategory.entrySet()) {
            VendorType vendorType = new VendorType();
            vendorType.setName(entry.getKey());
            vendorType.setVendors(entry.getValue());
            vendorTypes.add(vendorType);
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
     * Save a vendor
     */
    public Vendor saveVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }
    
    /**
     * Delete a vendor
     */
    public void deleteVendor(Integer id) {
        vendorRepository.deleteById(id);
    }
} 