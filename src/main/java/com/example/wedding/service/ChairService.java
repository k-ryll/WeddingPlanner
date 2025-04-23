package com.example.wedding.service;

import com.example.wedding.model.Chair;
import com.example.wedding.model.Guest;
import com.example.wedding.model.WeddingTable;
import com.example.wedding.repository.ChairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChairService {
    @Autowired
    private ChairRepository chairRepository;

    @Transactional
    public Chair assignGuestToChair(Integer chairId, Guest guest) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new RuntimeException("Chair not found"));
        chair.setGuest(guest);
        return chairRepository.save(chair);
    }

    @Transactional
    public void removeGuestFromChair(Integer chairId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new RuntimeException("Chair not found"));
        chair.setGuest(null);
        chairRepository.save(chair);
    }

    public List<Chair> getChairsByTable(WeddingTable table) {
        return chairRepository.findByWeddingTableId(table.getId());
    }

    public Chair getChairById(Integer chairId) {
        return chairRepository.findById(chairId).orElse(null);
    }
} 