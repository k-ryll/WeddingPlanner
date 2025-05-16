package com.example.wedding.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vendor")
public class Vendor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    
    private String category;
    
    private String location;
    
    @Column(name = "price_type")
    private String priceType; // "fixed" or "per_guest"
    
    @Column(name = "price_per_guest")
    private BigDecimal pricePerGuest;
    
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    
    private String contact;
    
    @Column(length = 1000)
    private String description;
    
    // Default constructor
    public Vendor() {
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getPriceType() {
        return priceType;
    }
    
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
    
    public BigDecimal getPricePerGuest() {
        return pricePerGuest;
    }
    
    public void setPricePerGuest(BigDecimal pricePerGuest) {
        this.pricePerGuest = pricePerGuest;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Helper method to check if price is per guest
    public boolean isPricePerGuest() {
        return "per_guest".equals(priceType);
    }
    
    // Calculate price based on guest count (if applicable)
    public BigDecimal calculatePrice(int guestCount) {
        if (isPricePerGuest() && pricePerGuest != null) {
            return pricePerGuest.multiply(new BigDecimal(guestCount));
        }
        return totalPrice;
    }
    
    // For UI display, get the average price (either total or per guest)
    public BigDecimal getAveragePrice() {
        return isPricePerGuest() ? pricePerGuest : totalPrice;
    }
} 