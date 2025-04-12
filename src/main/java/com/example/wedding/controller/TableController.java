package com.example.wedding.controller;

import com.example.wedding.dto.TableDTO;
import com.example.wedding.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    @PostMapping
    public ResponseEntity<TableDTO> createTable(@RequestBody TableDTO tableDTO, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null && session.getAttribute("adminLoggedIn") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        TableDTO createdTable = tableService.createTable(tableDTO);
        return ResponseEntity.ok(createdTable);
    }

    @PostMapping("/save-all")
    public ResponseEntity<List<TableDTO>> saveAllTables(@RequestBody List<TableDTO> tables, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null && session.getAttribute("adminLoggedIn") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<TableDTO> savedTables = tableService.saveAllTables(tables);
        return ResponseEntity.ok(savedTables);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TableDTO>> getTablesByProject(@PathVariable Integer projectId, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null && session.getAttribute("adminLoggedIn") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<TableDTO> tables = tableService.getTablesByProject(projectId);
        return ResponseEntity.ok(tables);
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deleteTable(@PathVariable Integer tableId, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null && session.getAttribute("adminLoggedIn") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        tableService.deleteTable(tableId);
        return ResponseEntity.ok().build();
    }
} 