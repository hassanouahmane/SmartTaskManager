package com.example.taskmanager.Controllers;

import com.example.taskmanager.Entities.*;

import com.example.taskmanager.Services.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService; // pour récupérer l'utilisateur

    public TaskController(TaskService taskService, AuthService authService) {
        this.taskService = taskService;
        this.authService = authService;
    }

    // ➕ CREATE task pour un utilisateur
    @PostMapping("/create/{userId}")
    public Task createTask(@PathVariable Long userId, @RequestBody Task task) {
        User user = authService.getUserById(userId);
        return taskService.createTask(task, user);
    }

    // 📄 READ ALL
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // 🔍 READ BY ID
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // ✏️ UPDATE
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    // ❌ DELETE
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    // 📄 LIST BY USER
    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Long userId) {
        User user = authService.getUserById(userId);
        return taskService.getTasksByUser(user);
    }
}
