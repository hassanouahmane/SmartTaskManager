package com.example.taskmanager.Services;

import com.example.taskmanager.Entities.Task;
import com.example.taskmanager.Entities.User;
import com.example.taskmanager.Repositorys.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // CREATE avec utilisateur
    public Task createTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    // READ ALL
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // READ BY ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task non trouvée"));
    }

    // UPDATE
    public Task updateTask(Long id, Task newTask) {
        Task task = getTaskById(id);
        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());
        task.setCompleted(newTask.isCompleted());
        return taskRepository.save(task);
    }

    // DELETE
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // LIST BY USER
    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUserId(user.getId());
    }
}
