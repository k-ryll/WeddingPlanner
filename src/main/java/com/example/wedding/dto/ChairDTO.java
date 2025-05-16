package com.example.wedding.dto;

public class ChairDTO {
    private Integer id;
    private Integer position;
    private GuestDTO guest;
    
    public ChairDTO() {}
    
    public ChairDTO(Integer id, Integer position, GuestDTO guest) {
        this.id = id;
        this.position = position;
        this.guest = guest;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public GuestDTO getGuest() {
        return guest;
    }
    
    public void setGuest(GuestDTO guest) {
        this.guest = guest;
    }
} 