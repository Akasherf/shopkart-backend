package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.LoginRequestDTO;
import com.ecommerce_backend.dto.SignupRequestDTO;
import com.ecommerce_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequestDTO dto) {
        userService.signup(dto);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequestDTO dto) {
        return userService.login(dto);
    }

}
