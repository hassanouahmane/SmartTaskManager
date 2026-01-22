package com.example.taskmanager.Repositorys;

import com.example.taskmanager.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // optionnel : tâches par utilisateur
    List<Task> findByUserId(Long userId);
}
