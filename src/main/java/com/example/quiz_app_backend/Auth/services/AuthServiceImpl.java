package com.example.quiz_app_backend.Auth.services;

import com.example.quiz_app_backend.Auth.dto.SignupRequest;
import com.example.quiz_app_backend.Auth.dto.UserDto;
import com.example.quiz_app_backend.Auth.repository.CustomerRepository;
import com.example.quiz_app_backend.entities.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User createCustomer(SignupRequest signupRequest) {
        //Check if customer already exist
        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            return null;
        }

        User user = new User();
        BeanUtils.copyProperties(signupRequest, user);

        //Hash the password before saving
        String hashPassword = passwordEncoder.encode(signupRequest.getPassword());
        user.setPassword(hashPassword);
        User createdUser = customerRepository.save(user);
        user.setId(createdUser.getId());
        return user;
    }
    // 1. Get all customers
    public List<UserDto> getCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 2. Delete customer by ID
    public boolean deleteById(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }


    // 3. Search customers by name
    public List<UserDto> searchByName(String name) {
        return customerRepository.findByUsernameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 4. Update customer details
    public UserDto update(Long id, UserDto userDto) {
        Optional<User> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            User user = optionalCustomer.get();
            // Update only the necessary fields
            user.setUsername(userDto.getName());
            user.setEmail(userDto.getEmail());

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            User updatedUser = customerRepository.save(user);
            return convertToDto(updatedUser);
        }
        return null;
    }

    // Utility method to convert Customer to CustomerDto
    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }
}

