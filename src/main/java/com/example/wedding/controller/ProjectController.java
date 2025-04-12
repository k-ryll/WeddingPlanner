package com.example.wedding.controller;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.wedding.model.Guest;
import com.example.wedding.model.Project;
import com.example.wedding.model.Task;
import com.example.wedding.model.ItineraryItem;
import com.example.wedding.service.GuestService;
import com.example.wedding.service.ProjectService;
import com.example.wedding.service.TaskService;
import com.example.wedding.service.ItineraryService;

@Controller
public class ProjectController {
    @Autowired
    private GuestService guestService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ItineraryService itineraryService;

    @GetMapping("/project/{id}")
    public String showProjectPage(@PathVariable Integer id, Model model) {
        model.addAttribute("projectId", id);
        return "project";
    }
    
    @GetMapping("/project/{id}/guest")
    public String showGuestsPage(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);  
        List<Guest> guests = guestService.findByProject(project);

        model.addAttribute("guests", guests);
        model.addAttribute("projectId", id);

        return "guestAdmin";
    }

    @GetMapping("/project/{id}/planning")
    public String showPlanningPage(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);
        List<Task> tasks = taskService.findByProject(project);
        List<ItineraryItem> itinerary = itineraryService.findByProject(project);

        model.addAttribute("projectId", id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("itinerary", itinerary);

        return "planning";
    }

    @PostMapping("/project/{id}/task/add")
    public String addTask(@PathVariable Integer id, @RequestParam String title, 
                         @RequestParam(required = false) String description,
                         @RequestParam String dueDate,
                         @RequestParam String priority) {
        Project project = projectService.findById(id);
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(LocalDate.parse(dueDate));
        task.setPriority(priority);
        task.setProject(project);
        taskService.save(task);
        return "redirect:/project/" + id + "/planning";
    }

    @PostMapping("/project/{taskId}/task/toggle")
    @ResponseBody
    public String toggleTask(@PathVariable Integer taskId, @RequestBody Task task) {
        Task existingTask = taskService.findById(taskId);
        if (existingTask != null) {
            existingTask.setCompleted(task.isCompleted());
            taskService.save(existingTask);
            return "{\"success\": true}";
        }
        return "{\"success\": false}";
    }

    @DeleteMapping("/project/{taskId}/task/delete")
    @ResponseBody
    public String deleteTask(@PathVariable Integer taskId) {
        taskService.delete(taskId);
        return "{\"success\": true}";
    }

    @PostMapping("/project/{id}/itinerary/add")
    public String addItineraryItem(@PathVariable Integer id, 
                                 @RequestParam String title,
                                 @RequestParam(required = false) String description,
                                 @RequestParam String startTime,
                                 @RequestParam String endTime,
                                 @RequestParam(required = false) String location) {
        Project project = projectService.findById(id);
        ItineraryItem item = new ItineraryItem();
        item.setTitle(title);
        item.setDescription(description);
        item.setStartTime(LocalTime.parse(startTime));
        item.setEndTime(LocalTime.parse(endTime));
        item.setLocation(location);
        item.setProject(project);
        itineraryService.save(item);
        return "redirect:/project/" + id + "/planning";
    }

    @DeleteMapping("/project/{itemId}/itinerary/delete")
    @ResponseBody
    public String deleteItineraryItem(@PathVariable Integer itemId) {
        itineraryService.delete(itemId);
        return "{\"success\": true}";
    }
}

