package com.stack.park.exceptions;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.validation.FieldError;

@ControllerAdvice // indique qu'elle va gérer les exceptions de manière global
public class GlobalExceptionHandler {

    // lorsque qu'un méthod annoté avec @Valid échoue à la validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // définit le status HTTP à renvoyé
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Parcourir toutes les erreurs de validation
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            // Obtenir le nom du champ ayant l'erreur
            String fieldName = ((FieldError) error).getField();
            // Obtenir les messages d'erreur
            String errorMessage = error.getDefaultMessage();
            // Ajouter l'erreur dans le map
            errors.put(fieldName, errorMessage);    
        });

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Failed to create PARK");
        errorResponse.put("details", errors.toString());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // lorsque les contraintes de validation sont violées
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        // Crée une map pour stocker les violations de contraintes
        Map<String, String> errors = new HashMap<>();
        // Parcourt toutes les violations de contraintes
        ex.getConstraintViolations().forEach(violation -> {
            // Obtient le nom de la propriété ayant la violation
            String fieldName = violation.getPropertyPath().toString();
            // Obtient le message de la violation
            String errorMessage = violation.getMessage();
            // Ajoute la violation dans la map
            errors.put(fieldName, errorMessage);
        });
        // Retourne une réponse HTTP avec le statut 400 (Bad Request) et la map des violations
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // lorsqu'une méthod a été passée un argument illégal ou inapproprié
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Lorsqu'un argument de méthod de contrôleur n'est pas du bon type
    // lorsque le type de l'argument ne correspond pas, comme une date mal formé
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMistach(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Invalid date format. Please use the format YYYY-MM-DD";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", errorMessage));
    }

    // lorsque l'analyse (parsing) d'une chaîne en une date/heure échoue.
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleDateTimeParseException(DateTimeParseException ex) {
        String errorMessage = "Invalid date format. Please use the format YYYY-MM-DD.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", errorMessage));
    }
}


