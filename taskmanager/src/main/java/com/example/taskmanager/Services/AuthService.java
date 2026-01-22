package com.example.taskmanager.Services;

import com.example.taskmanager.Entities.User;
import com.example.taskmanager.Repositorys.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register
    public User register(User user) {
        return userRepository.save(user);
    }

    // Login
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        return user;
    }
}
