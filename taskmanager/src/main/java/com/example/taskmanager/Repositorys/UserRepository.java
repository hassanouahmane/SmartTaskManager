package com.example.taskmanager.Repositorys;

import com.example.taskmanager.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Override
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

}
