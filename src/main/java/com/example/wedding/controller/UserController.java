package com.example.wedding.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.dto.UserDto;
import com.example.wedding.model.User;
import com.example.wedding.model.Project;
import com.example.wedding.model.Task;
import com.example.wedding.model.ItineraryItem;
import com.example.wedding.model.BudgetCategory;
import com.example.wedding.model.Expense;
import com.example.wedding.service.DuplicateEmailException;
import com.example.wedding.service.EmailService;
import com.example.wedding.service.UserService;
import com.example.wedding.service.VerificationService;
import com.example.wedding.service.ProjectService;
import com.example.wedding.service.TaskService;
import com.example.wedding.service.ItineraryService;
import com.example.wedding.service.BudgetService;
import com.example.wedding.service.GuestService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ItineraryService itineraryService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private GuestService guestService;

    @GetMapping("/index")
    public String showIndexPage() {
        return "index"; 
    }

    @GetMapping("/home")
    public String home(@SessionAttribute(name = "loggedUser", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        // Get the user's project
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        model.addAttribute("project", project); // Add project to model regardless of null
        
        if (project != null) {
            List<Task> tasks = taskService.findByProject(project);
            List<ItineraryItem> itinerary = itineraryService.findByProject(project);
            List<BudgetCategory> budgetCategories = budgetService.findByProject(project);
            BigDecimal totalBudget = budgetService.getTotalBudget(project);
            BigDecimal totalSpent = budgetService.getTotalSpent(project);
            // Guest summary
            List<com.example.wedding.model.Guest> guests = guestService.findByProject(project);
            long totalGuests = guests.size();
            long confirmedGuests = guests.stream().filter(g -> g.getRsvp() != null && g.getRsvp().equalsIgnoreCase("ACCEPTED")).count();
            long pendingGuests = guests.stream().filter(g -> g.getRsvp() != null && g.getRsvp().equalsIgnoreCase("PENDING")).count();
            
            model.addAttribute("tasks", tasks);
            model.addAttribute("itinerary", itinerary);
            model.addAttribute("budgetCategories", budgetCategories);
            model.addAttribute("totalBudget", totalBudget);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("totalGuests", totalGuests);
            model.addAttribute("confirmedGuests", confirmedGuests);
            model.addAttribute("pendingGuests", pendingGuests);
            model.addAttribute("guests", guests);
        } else {
            // Initialize empty collections and zero values when no project exists
            model.addAttribute("tasks", Collections.emptyList());
            model.addAttribute("itinerary", Collections.emptyList());
            model.addAttribute("budgetCategories", Collections.emptyList());
            model.addAttribute("totalBudget", BigDecimal.ZERO);
            model.addAttribute("totalSpent", BigDecimal.ZERO);
            model.addAttribute("totalGuests", 0);
            model.addAttribute("confirmedGuests", 0);
            model.addAttribute("pendingGuests", 0);
            model.addAttribute("guests", Collections.emptyList());
        }
        
        model.addAttribute("user", user);
        return "home"; 
    }

    @GetMapping("/planning")
    public String planning(@SessionAttribute(name = "loggedUser", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        // Get the user's project
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            // Add necessary data to model for planning page
            List<Task> tasks = taskService.findByProject(project);
            List<ItineraryItem> itinerary = itineraryService.findByProject(project);
            List<BudgetCategory> budgetCategories = budgetService.findByProject(project);
            BigDecimal totalBudget = budgetService.getTotalBudget(project);
            BigDecimal totalSpent = budgetService.getTotalSpent(project);
            
            model.addAttribute("projectId", project.getId());
            model.addAttribute("tasks", tasks);
            model.addAttribute("itinerary", itinerary);
            model.addAttribute("budgetCategories", budgetCategories);
            model.addAttribute("totalBudget", totalBudget);
            model.addAttribute("totalSpent", totalSpent);
            
            return "user_planning";
        } else {
            // Handle case when user has no project
            model.addAttribute("error", "No project found. Please create a project first.");
            return "home";
        }
    }

    @GetMapping("/user/login")
    public String showLoginPage(Model model) {
    	model.addAttribute("userDto", new UserDto());  
        return "Login"; 
    }

    @GetMapping("/user/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }
    
    @PostMapping("/user/validate")
    public String loginUser(
            @RequestParam("email") String email, 
            @RequestParam("password") String password, 
            RedirectAttributes redi, 
            HttpSession session) {
        
        System.out.println("Email: " + email);
        User user = service.findByEmail(email);
        
        if (user != null) {
            System.out.println("User found: " + user.getEmail());
        } else {
            System.out.println("User not found!");
        }
        
        if (user != null && BCrypt.checkpw(password, user.getPassword()) && user.isVerified()) {
            
            session.setAttribute("loggedUser", user);
            

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            

            SecurityContextHolder.getContext().setAuthentication(auth);
            
      
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
         
            return "redirect:/home";
        } else {
            redi.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/user/login";
        }
    }

@PostMapping("/user/save")
public String saveUser(@RequestParam("password") String password,
                       @RequestParam("confirmPass") String confirmPass,
                       @RequestParam("email") String email,
                       @ModelAttribute User user,
                       RedirectAttributes redi) {
    try {
        if (!password.equals(confirmPass)) {
            redi.addFlashAttribute("error", "Passwords do not match!");
            redi.addFlashAttribute("user", user);
            return "redirect:/user/signup";
        }   
        
        service.save(user);

        String verificationCode = verificationService.generateVerificationCode(user);
        
        try {
            emailService.sendVerificationEmail(email, verificationCode);
        } catch (Exception e) {
            redi.addFlashAttribute("error", "Failed to send verification email. Please try again.");
            return "redirect:/user/signup";
        }
        
        redi.addFlashAttribute("message", "A verification code has been sent to your email. Please verify your account.");
        return "redirect:/verify?email=" + email;

    } catch (DuplicateEmailException e) {
        redi.addFlashAttribute("error", e.getMessage());
        redi.addFlashAttribute("user", user);
        return "redirect:/user/signup";
    }
}

    @PostMapping("/budget/add")
    public String addBudgetExpense(@SessionAttribute(name = "loggedUser", required = false) User user,
                                   @RequestParam("categoryId") Integer categoryId,
                                   @RequestParam("name") String name,
                                   @RequestParam("amount") BigDecimal amount,
                                   @RequestParam("date") String date,
                                   @RequestParam(value = "description", required = false) String description) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            try {
                // Find the budget category
                BudgetCategory category = budgetService.findByProject(project)
                    .stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Category not found"));

                // Create and save expense
                Expense expense = new Expense();
                expense.setName(name);
                expense.setAmount(amount);
                expense.setDate(LocalDate.parse(date));
                expense.setDescription(description);
                expense.setCategory(category);
                budgetService.saveExpense(expense);
                
                return "redirect:/planning";
            } catch (Exception e) {
                return "redirect:/planning?error=" + e.getMessage();
            }
        }
        return "redirect:/planning";
    }
    
    @PostMapping("/budget/category/add")
    public String addBudgetCategory(@SessionAttribute(name = "loggedUser", required = false) User user,
                                   @RequestParam("name") String name,
                                   @RequestParam("budget") BigDecimal budget,
                                   @RequestParam(value = "description", required = false) String description) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            try {
                // Create and save category
                BudgetCategory category = new BudgetCategory();
                category.setName(name);
                category.setBudget(budget);
                category.setDescription(description);
                category.setProject(project);
                budgetService.saveCategory(category);
                
                return "redirect:/planning";
            } catch (Exception e) {
                return "redirect:/planning?error=" + e.getMessage();
            }
        }
        return "redirect:/planning";
    }
    
    @PostMapping("/task/add")
    public String addTask(@SessionAttribute(name = "loggedUser", required = false) User user,
                         @RequestParam("title") String title,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam("dueDate") String dueDate,
                         @RequestParam("priority") String priority) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            try {
                // Create and save task
                Task task = new Task();
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(LocalDate.parse(dueDate));
                task.setPriority(priority);
                task.setProject(project);
                taskService.save(task);
                
                return "redirect:/planning";
            } catch (Exception e) {
                return "redirect:/planning?error=" + e.getMessage();
            }
        }
        return "redirect:/planning";
    }
    
    @PostMapping("/itinerary/add")
    public String addItineraryItem(@SessionAttribute(name = "loggedUser", required = false) User user,
                                 @RequestParam("title") String title,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime,
                                 @RequestParam(value = "location", required = false) String location) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            try {
                // Create and save itinerary item
                ItineraryItem item = new ItineraryItem();
                item.setTitle(title);
                item.setDescription(description);
                item.setStartTime(LocalTime.parse(startTime));
                item.setEndTime(LocalTime.parse(endTime));
                item.setLocation(location);
                item.setProject(project);
                itineraryService.save(item);
                
                return "redirect:/planning";
            } catch (Exception e) {
                return "redirect:/planning?error=" + e.getMessage();
            }
        }
        return "redirect:/planning";
    }
    
    @PostMapping("/task/{taskId}/send-email")
    @ResponseBody
    public ResponseEntity<?> sendTaskEmail(@SessionAttribute(name = "loggedUser", required = false) User user,
                               @PathVariable("taskId") Integer taskId,
                               @RequestParam("entourageType") String entourageType) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            try {
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
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/itinerary/send-email")
    @ResponseBody
    public ResponseEntity<?> sendItineraryEmail(@SessionAttribute(name = "loggedUser", required = false) User user,
                                    @RequestParam("entourageType") String entourageType) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project != null) {
            try {
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
        return ResponseEntity.notFound().build();
    }
}
