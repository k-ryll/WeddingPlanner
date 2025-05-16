package com.example.wedding.controller;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.Guest;
import com.example.wedding.model.Project;
import com.example.wedding.model.Task;
import com.example.wedding.model.ItineraryItem;
import com.example.wedding.model.BudgetCategory;
import com.example.wedding.model.Expense;
import com.example.wedding.model.User;
import com.example.wedding.model.Vendor;
import com.example.wedding.service.GuestService;
import com.example.wedding.service.ProjectService;
import com.example.wedding.service.TaskService;
import com.example.wedding.service.ItineraryService;
import com.example.wedding.service.EmailService;
import com.example.wedding.service.BudgetService;
import com.example.wedding.service.UserService;
import com.example.wedding.service.VendorService;

import jakarta.servlet.http.HttpSession;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private UserService userService;
    @Autowired
    private VendorService vendorService;

    @GetMapping("/project/{id}")
    public String showProjectPage(@PathVariable Integer id, Model model, HttpSession session) {
        // Check if user is logged in
        User loggedUser = (User) session.getAttribute("loggedUser");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (loggedUser == null && !Boolean.TRUE.equals(isAdmin)) {
            return "redirect:/user/login";
        }

        Project project = projectService.findById(id);
        if (project == null) {
            return "redirect:/home";
        }

        List<Task> tasks = taskService.findByProject(project);
        List<ItineraryItem> itinerary = itineraryService.findByProject(project);
        List<BudgetCategory> budgetCategories = budgetService.findByProject(project);
        
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;
        
        // Calculate totals safely
        if (budgetCategories != null) {
            for (BudgetCategory category : budgetCategories) {
                if (category.getBudget() != null) {
                    totalBudget = totalBudget.add(category.getBudget());
                }
                if (category.getExpenses() != null) {
                    for (Expense expense : category.getExpenses()) {
                        if (expense.getAmount() != null) {
                            totalSpent = totalSpent.add(expense.getAmount());
                        }
                    }
                }
            }
        }
        
        // Guest summary
        List<Guest> guests = guestService.findByProject(project);
        long totalGuests = guests != null ? guests.size() : 0;
        long confirmedGuests = guests != null ? guests.stream()
            .filter(g -> g.getRsvp() != null && g.getRsvp().equalsIgnoreCase("ACCEPTED"))
            .count() : 0;
        long pendingGuests = guests != null ? guests.stream()
            .filter(g -> g.getRsvp() != null && g.getRsvp().equalsIgnoreCase("PENDING"))
            .count() : 0;
        
        model.addAttribute("project", project);
        model.addAttribute("projectId", id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("itinerary", itinerary);
        model.addAttribute("budgetCategories", budgetCategories);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("totalGuests", totalGuests);
        model.addAttribute("confirmedGuests", confirmedGuests);
        model.addAttribute("pendingGuests", pendingGuests);
        model.addAttribute("guests", guests);
        return "project";
    }
    
    @GetMapping("/project/{id}/guest")
    public String showGuestsPage(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);  
        List<Guest> guests = guestService.findByProject(project);

        model.addAttribute("guests", guests);
        model.addAttribute("projectId", id);
        model.addAttribute("guest", new Guest());
        return "guestAdmin";
    }

    @PostMapping("/project/{id}/guest/add")
    public String addGuest(@PathVariable Integer id,
                           @RequestParam String title, 
                           @RequestParam String name,
                           @RequestParam String email, 
                           @RequestParam(required = false) String phone,
                           @RequestParam(defaultValue = "Pending") String rsvp, 
                           @RequestParam String entourage, 
                           @RequestParam(required = false) String remarks,
                           HttpSession session, 
                           RedirectAttributes redirectAttributes) {

        // Check if user is logged in as a regular user OR as an admin
        User loggedUser = (User) session.getAttribute("loggedUser");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (loggedUser == null && !Boolean.TRUE.equals(isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to add a guest.");
            return "redirect:/user/login"; 
        }

        Project project = projectService.findById(id);
        if (project == null) {
            redirectAttributes.addFlashAttribute("error", "Project not found.");
            return "redirect:/project"; // Redirect to projects list or appropriate page
        }

        Guest guest = new Guest();
        guest.setTitle(title);
        guest.setName(name);
        guest.setEmail(email);
        guest.setPhone(phone);
        guest.setRsvp(rsvp);
        guest.setEntourage(entourage);
        guest.setRemarks(remarks);
        guest.setProjectId(project);
        
        // If we have a logged-in user, set them as the addedBy user
        // If we're in admin mode without a logged-in user, we need to handle this differently
        if (loggedUser != null) {
            guest.setAddedBy(loggedUser);
        } else if (Boolean.TRUE.equals(isAdmin)) {
            // For admin: try to use a default user or the project's primary user
            // This depends on your business logic: who should be shown as having "added" the guest?
            
            // Option 1: Use project's bride/groom/organizer if available
            User projectUser = null;
            if (project.getBride() != null) {
                projectUser = project.getBride();
            }
            if (projectUser == null && project.getGroom() != null) {
                projectUser = project.getGroom();
            }
            if (projectUser == null && project.getOrganizer() != null) {
                projectUser = project.getOrganizer();
            }
            
            // If we found a project user, use them; otherwise show error
            if (projectUser != null) {
                guest.setAddedBy(projectUser);
            } else {
                redirectAttributes.addFlashAttribute("error", "Cannot add guest: no project user available to associate with the guest.");
                return "redirect:/project/" + id + "/guest";
            }
        }

        guestService.save(guest);
        redirectAttributes.addFlashAttribute("message", "Guest added successfully!");
        return "redirect:/project/" + id + "/guest";
    }

    @PostMapping("/project/{id}/guest/edit")
    public String editGuest(@PathVariable Integer id,
                           @RequestParam Integer guestId,
                           @RequestParam String title,
                           @RequestParam String name,
                           @RequestParam String email,
                           @RequestParam(required = false) String phone,
                           @RequestParam String entourage,
                           @RequestParam String rsvp,
                           @RequestParam(required = false) String remarks,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        // Check if user is logged in as a regular user OR as an admin
        User loggedUser = (User) session.getAttribute("loggedUser");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (loggedUser == null && !Boolean.TRUE.equals(isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to edit a guest.");
            return "redirect:/user/login"; 
        }

        Guest guest = guestService.findById(guestId);
        if (guest == null) {
            redirectAttributes.addFlashAttribute("error", "Guest not found!");
            return "redirect:/project/" + id + "/guest";
        }
        
        // Update guest details
        guest.setTitle(title);
        guest.setName(name);
        guest.setEmail(email);
        if (phone != null) guest.setPhone(phone);
        guest.setEntourage(entourage);
        guest.setRsvp(rsvp);
        guest.setRemarks(remarks);
        
        guestService.save(guest);
        redirectAttributes.addFlashAttribute("message", "Guest updated successfully!");
        return "redirect:/project/" + id + "/guest";
    }

    @GetMapping("/project/{id}/planning")
    public String showPlanningPage(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);
        List<Task> tasks = taskService.findByProject(project);
        List<ItineraryItem> itinerary = itineraryService.findByProject(project);
        List<BudgetCategory> budgetCategories = budgetService.findByProject(project);

        BigDecimal totalBudget = budgetService.getTotalBudget(project);
        BigDecimal totalSpent = budgetService.getTotalSpent(project);

        model.addAttribute("projectId", id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("itinerary", itinerary);
        model.addAttribute("budgetCategories", budgetCategories);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalSpent", totalSpent);

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

    @PostMapping("/project/{id}/task/{taskId}/send-email")
    @ResponseBody
    public ResponseEntity<?> sendTaskEmail(
            @PathVariable Integer id,
            @PathVariable Integer taskId,
            @RequestParam String entourageType) {
        try {
            Project project = projectService.findById(id);
            if (project == null) {
                return ResponseEntity.notFound().build();
            }

            Task task = taskService.findById(taskId);
            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            if ("ALL".equals(entourageType)) {
                emailService.sendTaskToAllEntourage(
                    project,
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().toString()
                );
            } else {
                emailService.sendTaskToEntourage(
                    project,
                    entourageType,
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().toString()
                );
            }

            return ResponseEntity.ok("Task email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send task email: " + e.getMessage());
        }
    }

    @PostMapping("/project/{id}/itinerary/send-email")
    @ResponseBody
    public ResponseEntity<?> sendItineraryEmail(
            @PathVariable Integer id,
            @RequestParam String entourageType) {
        try {
            Project project = projectService.findById(id);
            if (project == null) {
                return ResponseEntity.notFound().build();
            }

            List<ItineraryItem> itinerary = itineraryService.findByProject(project);
            if (itinerary.isEmpty()) {
                return ResponseEntity.badRequest().body("No itinerary items found");
            }

            if ("ALL".equals(entourageType)) {
                emailService.sendItineraryToAllEntourage(project, itinerary);
            } else {
                emailService.sendItineraryToEntourage(project, entourageType, itinerary);
            }

            return ResponseEntity.ok("Itinerary email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send itinerary email: " + e.getMessage());
        }
    }

    @PostMapping("/project/{id}/budget/category/add")
    public String addBudgetCategory(@PathVariable Integer id,
                                  @RequestParam String name,
                                  @RequestParam BigDecimal budget,
                                  @RequestParam(required = false) String description) {
        Project project = projectService.findById(id);
        BudgetCategory category = new BudgetCategory();
        category.setName(name);
        category.setBudget(budget);
        category.setDescription(description);
        category.setProject(project);
        budgetService.saveCategory(category);
        return "redirect:/project/" + id + "/budget";
    }

    @PostMapping("/project/{id}/budget/add")
    public String addExpense(@PathVariable Integer id,
                           @RequestParam Integer categoryId,
                           @RequestParam String name,
                           @RequestParam BigDecimal amount,
                           @RequestParam String date,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) Integer vendorId) {
        BudgetCategory category = budgetService.findByProject(projectService.findById(id))
                .stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Expense expense = new Expense();
        expense.setName(name);
        expense.setAmount(amount);
        expense.setDate(LocalDate.parse(date));
        expense.setDescription(description);
        expense.setCategory(category);
        
        // If a vendor was selected, add it to the expense
        if (vendorId != null) {
            Vendor vendor = vendorService.getVendorById(vendorId);
            if (vendor != null) {
                expense.setVendor(vendor);
            }
        }
        
        budgetService.saveExpense(expense);
        return "redirect:/project/" + id + "/budget";
    }

    @DeleteMapping("/project/{id}/budget/category/{categoryId}/delete")
    @ResponseBody
    public String deleteBudgetCategory(@PathVariable Integer id, @PathVariable Integer categoryId) {
        budgetService.deleteCategory(categoryId);
        return "{\"success\": true}";
    }

    @DeleteMapping("/project/{id}/budget/expense/{expenseId}/delete")
    @ResponseBody
    public String deleteExpense(@PathVariable Integer id, @PathVariable Integer expenseId) {
        budgetService.deleteExpense(expenseId);
        return "{\"success\": true}";
    }
    
    @PostMapping("/project/{id}/budget/expense/{expenseId}/delete")
    public String deleteExpensePost(@PathVariable Integer id, @PathVariable Integer expenseId) {
        budgetService.deleteExpense(expenseId);
        return "redirect:/project/" + id + "/budget";
    }
    
    @PostMapping("/project/{id}/budget/category/{categoryId}/delete")
    public String deleteCategoryPost(@PathVariable Integer id, @PathVariable Integer categoryId) {
        budgetService.deleteCategory(categoryId);
        return "redirect:/project/" + id + "/budget";
    }

    @PostMapping("/project/{id}/essential-details/update")
    public String updateEssentialDetails(@PathVariable Integer id,
                                       @RequestParam String brideName,
                                       @RequestParam String groomName,
                                       @RequestParam String weddingDate,
                                       @RequestParam(required = false) String maidOfHonor,
                                       @RequestParam(required = false) String bestMan,
                                       RedirectAttributes redirectAttributes) {
        try {
            Project project = projectService.findById(id);
            if (project == null) {
                redirectAttributes.addFlashAttribute("error", "Project not found.");
                return "redirect:/project/" + id;
            }

            // Split names into first and last names
            String[] brideNames = brideName.split(" ", 2);
            String[] groomNames = groomName.split(" ", 2);
            
            // Update bride
            User bride = project.getBride();
            if (bride != null) {
                bride.setFirstName(brideNames[0]);
                bride.setLastName(brideNames.length > 1 ? brideNames[1] : "");
                userService.save(bride);
            }

            // Update groom
            User groom = project.getGroom();
            if (groom != null) {
                groom.setFirstName(groomNames[0]);
                groom.setLastName(groomNames.length > 1 ? groomNames[1] : "");
                userService.save(groom);
            }

            // Update wedding date
            project.setWeddingDate(LocalDate.parse(weddingDate).atStartOfDay());

            // Update best man and maid of honor if provided
            if (bestMan != null && !bestMan.isEmpty()) {
                User bestManUser = userService.findByEmail(bestMan);
                if (bestManUser != null) {
                    project.setBestMan(bestManUser);
                }
            }
            if (maidOfHonor != null && !maidOfHonor.isEmpty()) {
                User maidOfHonorUser = userService.findByEmail(maidOfHonor);
                if (maidOfHonorUser != null) {
                    project.setMaidOfHonor(maidOfHonorUser);
                }
            }

            projectService.updateProject(
                project.getId(),
                project.getProjectName(),
                groom.getEmail(),
                bride.getEmail(),
                project.getOrganizer() != null ? project.getOrganizer().getEmail() : null,
                project.getWeddingDate().toString(),
                project.getStatus()
            );
            redirectAttributes.addFlashAttribute("message", "Essential details updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update essential details: " + e.getMessage());
        }
        return "redirect:/project/" + id;
    }

    @GetMapping("/project/{id}/budget")
    public String showBudgetPage(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);
        if (project == null) {
            return "redirect:/home";
        }

        List<BudgetCategory> budgetCategories = budgetService.findByProject(project);
        BigDecimal totalBudget = budgetService.getTotalBudget(project);
        BigDecimal totalSpent = budgetService.getTotalSpent(project);

        // Get all vendor types for the dropdown
        List<VendorService.VendorType> vendorTypes = vendorService.getVendorsByType();
        
        // Get distinct vendor categories for category suggestions
        List<String> vendorCategories = vendorService.getDistinctVendorCategories();
        
        // Get total guest count for price calculations
        int totalGuests = guestService.findByProject(project).size();
        
        // Get all vendors used in this project's expenses
        List<Vendor> usedVendors = new ArrayList<>();
        for (BudgetCategory category : budgetCategories) {
            for (Expense expense : category.getExpenses()) {
                if (expense.getVendor() != null && !usedVendors.contains(expense.getVendor())) {
                    usedVendors.add(expense.getVendor());
                }
            }
        }

        model.addAttribute("projectId", id);
        model.addAttribute("budgetCategories", budgetCategories);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("vendorTypes", vendorTypes);
        model.addAttribute("vendorCategories", vendorCategories);
        model.addAttribute("totalGuests", totalGuests);
        model.addAttribute("usedVendors", usedVendors);

        return "budget";
    }

    @PostMapping("/project/{id}/budget/category/edit")
    public String editBudgetCategory(@PathVariable Integer id,
                                  @RequestParam Integer categoryId,
                                  @RequestParam String name,
                                  @RequestParam BigDecimal budget,
                                  @RequestParam(required = false) String description) {
        // Find the category from the project's categories
        BudgetCategory category = budgetService.findByProject(projectService.findById(id))
                .stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElse(null);
                
        if (category != null) {
            category.setName(name);
            category.setBudget(budget);
            category.setDescription(description);
            budgetService.saveCategory(category);
        }
        return "redirect:/project/" + id + "/budget";
    }
}

