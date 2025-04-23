package com.example.wedding.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "seat_plan")
public class SeatPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "seatPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeddingTable> tables;

    public SeatPlan() {}

    public SeatPlan(Project project) {
        this.project = project;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<WeddingTable> getTables() {
        return tables;
    }

    public void setTables(List<WeddingTable> tables) {
        this.tables = tables;
    }
} 