package com.example.wedding.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chairs")
public class Chair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private WeddingTable weddingTable;

    @Column(nullable = false)
    private Integer position; // Position of the chair around the table (1-12)

    @OneToOne
    @JoinColumn(name = "guest_id")
    private Guest guest; // Optional: if a guest is assigned to this chair

    public Chair() {}

    public Chair(WeddingTable weddingTable, Integer position) {
        this.weddingTable = weddingTable;
        this.position = position;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WeddingTable getWeddingTable() {
        return weddingTable;
    }

    public void setWeddingTable(WeddingTable weddingTable) {
        this.weddingTable = weddingTable;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }
} 