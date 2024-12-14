package com.example.quiz_app_backend.Auth.controllers;



import com.example.quiz_app_backend.Auth.dto.UserDto;
import com.example.quiz_app_backend.Auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class UserController {

    private final AuthService authService;

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    // 1. Obtenir tous les clients
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllCustomers() {
        List<UserDto> customers = authService.getCustomers();
        return ResponseEntity.ok(customers);
    }

    // 2. Supprimer un client par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = authService.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 3. Rechercher des clients par nom
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchCustomers(@RequestParam String name) {
        List<UserDto> customers = authService.searchByName(name);
        return ResponseEntity.ok(customers);
    }



    // 5. Mettre à jour un client
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedCustomer = authService.update(id, userDto);
        if (updatedCustomer != null) {
            return ResponseEntity.ok(updatedCustomer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
        }
    }
}
