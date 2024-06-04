package com.stack.park.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stack.park.entities.Park;
import com.stack.park.exceptions.UserNotFoundException;
import com.stack.park.services.ParkService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class ParkController {

    @Autowired
    private ParkService userService;

    @PostMapping
    public Park createUser(@Valid @RequestBody Park user) {
        return userService.create(user);
    }

    @GetMapping
    public List<Park> getAllUser() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Park>> getUserById(@PathVariable("id") Integer id) {
        Optional<Park> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public Park updateUserById(@PathVariable("id") Integer id, @RequestBody Park user) {
            return userService.updateById(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Integer id) {
        userService.deleteById(id);
    }
}
