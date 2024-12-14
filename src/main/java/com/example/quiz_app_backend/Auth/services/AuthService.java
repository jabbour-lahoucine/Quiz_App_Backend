package com.example.quiz_app_backend.Auth.services;



import com.example.quiz_app_backend.Auth.dto.SignupRequest;
import com.example.quiz_app_backend.Auth.dto.UserDto;
import com.example.quiz_app_backend.entities.User;

import java.util.List;

public interface AuthService {
    User createCustomer(SignupRequest signupRequest);
    public boolean deleteById(Long id);
    List<UserDto> searchByName(String name);
    UserDto update(Long id, UserDto userDto);
    List<UserDto> getCustomers();
}
