package com.ecommerce_backend.service;

import com.ecommerce_backend.dto.LoginRequestDTO;
import com.ecommerce_backend.dto.SignupRequestDTO;

public interface UserService {

    void signup(SignupRequestDTO dto);

    String login(LoginRequestDTO dto);
}
