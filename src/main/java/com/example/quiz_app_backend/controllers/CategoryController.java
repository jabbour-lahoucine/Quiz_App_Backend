package com.example.quiz_app_backend.controllers;

import com.example.quiz_app_backend.entities.Category;
import com.example.quiz_app_backend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/createCategory")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @GetMapping("/categorieByTitle/{title}")
    public Category findByTitle(@PathVariable String title) {
        return categoryService.findByTitle(title);
    }

    @GetMapping("/allCategories")
    public List<Category> findAll() {
        return categoryService.findAll();
    }


}
