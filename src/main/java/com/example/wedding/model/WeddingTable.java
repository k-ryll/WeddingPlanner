package com.example.wedding.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wedding_table")
public class WeddingTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seat_plan_id", nullable = false)
    private SeatPlan seatPlan;

    @Column(nullable = false)
    private String tableName;

    @Column(nullable = false)
    private Integer numberOfChairs;

    @Column(nullable = false)
    private Integer positionX;

    @Column(nullable = false)
    private Integer positionY;

    public WeddingTable() {}

    public WeddingTable(String tableName, Integer numberOfChairs, Integer positionX, Integer positionY) {
        this.tableName = tableName;
        this.numberOfChairs = numberOfChairs;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SeatPlan getSeatPlan() {
        return seatPlan;
    }

    public void setSeatPlan(SeatPlan seatPlan) {
        this.seatPlan = seatPlan;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getNumberOfChairs() {
        return numberOfChairs;
    }

    public void setNumberOfChairs(Integer numberOfChairs) {
        this.numberOfChairs = numberOfChairs;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }
} 