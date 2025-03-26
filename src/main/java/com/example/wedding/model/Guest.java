package com.example.wedding.model;

import jakarta.persistence.*;

@Entity
@Table(name = "guests")
public class Guest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer guestId;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 45)
    private String rsvp;

    @Column(nullable = false, length = 45)
    private String entourage;
    
    @ManyToOne
    @JoinColumn(name = "projectId", nullable = false)
    private Project projectId;
    
    // Establishing a Many-to-One relationship with User
    @ManyToOne
    @JoinColumn(name = "added_by", nullable = false) // This creates a foreign key column `added_by`
    private User addedBy;

    // Getters and Setters
    public Integer getGuestId() { return guestId; }
    public void setGuestId(Integer guestId) { this.guestId = guestId; }

    public Project getProjectId() {
		return projectId;
	}
	public void setProjectId(Project projectId) {
		this.projectId = projectId;
	}
	public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRsvp() { return rsvp; }
    public void setRsvp(String rsvp) { this.rsvp = rsvp; }

    public String getEntourage() { return entourage; }
    public void setEntourage(String entourage) { this.entourage = entourage; }

    public User getAddedBy() { return addedBy; }
    public void setAddedBy(User addedBy) { this.addedBy = addedBy; }
}
