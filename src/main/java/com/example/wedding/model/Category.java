package com.example.wedding.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "categories")
    private Set<Vendor> vendors = new HashSet<>();
    
    // Default constructor
    public Category() {
    }
    
    public Category(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<Vendor> getVendors() {
        return vendors;
    }
    
    public void setVendors(Set<Vendor> vendors) {
        this.vendors = vendors;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Category category = (Category) o;
        
        return id != null ? id.equals(category.id) : category.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 