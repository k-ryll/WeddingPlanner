package com.example.wedding.service;

import com.example.wedding.model.Task;
import com.example.wedding.model.Project;
import com.example.wedding.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findByProject(Project project) {
        return taskRepository.findByProject(project);
    }

    public List<Task> findIncompleteTasks(Project project) {
        return taskRepository.findByProjectAndCompleted(project, false);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void delete(Integer id) {
        taskRepository.deleteById(id);
    }

    public Task findById(Integer id) {
        return taskRepository.findById(id).orElse(null);
    }
} 