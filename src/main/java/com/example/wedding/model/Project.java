package com.example.wedding.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "project")
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	private Integer id;
	
	@Column(nullable = false, length = 100)
	private String projectName;
	
	@OneToOne
    @JoinColumn(name = "groom", nullable = false) 
    private User groom;
	
	@OneToOne
    @JoinColumn(name = "bride", nullable = false) 
    private User bride;
	
	@ManyToOne
    @JoinColumn(name = "organizer", nullable = true) 
    private User organizer;
	
    @Column(nullable = false)
    private LocalDateTime weddingDate;
    
    @Column(nullable = false)
    private String status;

    public Project() {}

    public Project(String projectName, User groom, User bride, User organizer, LocalDateTime weddingDate, String status) {
        this.projectName = projectName;
        this.groom = groom;
        this.bride = bride;
        this.organizer = organizer;
        this.weddingDate = weddingDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public User getGroom() {
        return groom;
    }

    public void setGroom(User groom) {
        this.groom = groom;
    }

    public User getBride() {
        return bride;
    }

    public void setBride(User bride) {
        this.bride = bride;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public LocalDateTime getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(LocalDateTime weddingDate) {
        this.weddingDate = weddingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", groom=" + groom +
                ", bride=" + bride +
                ", organizer=" + organizer +
                ", weddingDate=" + weddingDate +
                ", status='" + status + '\'' +
                '}';
    }
        
}
