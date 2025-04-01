package com.example.wedding.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ProjectController {
    @GetMapping("/project/{id}")
    public String showProjectPage(@RequestParam String param) {
        return "project";
    }
    
}
