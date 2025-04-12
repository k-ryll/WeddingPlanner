package com.example.wedding.repository;

import com.example.wedding.model.Task;
import com.example.wedding.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByProject(Project project);
    List<Task> findByProjectAndCompleted(Project project, boolean completed);
} 