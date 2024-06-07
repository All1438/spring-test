package com.stack.park.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.http.ResponseEntity;
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
import com.stack.park.exceptions.NotFoundException;
import com.stack.park.services.ParkService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/parks")
public class ParkController {

    @Autowired
    private ParkService parkService;

    /**
     * Ajouté un nouveau park
     * 
     * @param park Les informations du park à créer
     * @return Une réponse HTTP 201 (Created) contenant le park créer
     * @throws ResponseStatusException Si la création du park échoue
     */
    @PostMapping
    public ResponseEntity<?> createPark(@Valid @RequestBody Park park) {
        try {
            if (park.getOccupiedSpace() == null) {
                park.setOccupiedSpace(0);
            }

            Park createdPark = parkService.create(park);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPark);
        } catch (IllegalArgumentException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create park");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    

    /**
     * Récupère tous les parks
     * 
     * @return Une liste de tous les parks
     */
    @GetMapping
    public List<Park> getAllUser() {
        return parkService.findAll();
    }

    /**
     * Récupère un park par son identifiant
     * 
     * @param id L'identifiant du park à recupérer.
     * @return Une réponse HTTP 200 (Ok) contenant le park si trouvé, ou une réponse HTTP 404 (Not Found) si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Park>> getUserById(@PathVariable("id") Integer id) {
        try {
            Optional<Park> userOptional = parkService.findById(id);
    
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Met à jour les informations d'un park existant
     * 
     * @param id L'identifiant du park à mettre à jour
     * @param park Les nouvelles informations du park
     * @return Une réponse HTTP 200 (Ok) avec le park mis à jour
     * @throws ResponseStatusException Si le park avec l'ID spécifié n'est pas trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Park> updateParkById(@PathVariable("id") Integer id, @Valid @RequestBody Park park) {
        try {
            Park updatedPark = parkService.updateById(park, id);
            return ResponseEntity.ok(updatedPark);
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Supprime un park par son identifiant
     * 
     * @param id L'identifiant du park à supprimer
     * @return Une réponse HTTP 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Integer id) {
        parkService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    
}
