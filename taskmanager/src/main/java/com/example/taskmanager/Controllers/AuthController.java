package com.example.taskmanager.Controllers;

import com.example.taskmanager.dto.LoginRequest;
import com.example.taskmanager.Entities.User;
import com.example.taskmanager.Services.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // REGISTER
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    // LOGIN
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }
}
