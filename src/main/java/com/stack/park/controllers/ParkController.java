package com.stack.park.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stack.park.dto.ParkProjection;
import com.stack.park.entities.Park;
import com.stack.park.entities.ParkCapacityChange;
import com.stack.park.exceptions.NotFoundException;
import com.stack.park.services.ParkService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/parks")
@Validated
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
            Optional<Park> userOptional = parkService.findById(id);
    
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional);
            } else {
                return ResponseEntity.notFound().build(); // si on ne spécifie pas le NotFoundException dans ExceptionHandler alors ici il peut retourner une erreur 500
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

    /**
     * récupère les parks supérieur à 10000
     * 
     * @return Une réponse HTTP 200 (Ok)
     */
    @GetMapping("/high-capacity")
    public ResponseEntity<List<Park>> getParksWithHighCapacity() {
        List<Park> parks = parkService.getCapacityGreaterThan10000();
        return ResponseEntity.ok(parks);
    }

    /**
     * récupère les parks inférieur à 10000
     * 
     * @return Une réponse HTTP 200 (ok)
     */
    @GetMapping("/low-capacity")
    public ResponseEntity<List<Park>> getParksWithLowCapacity() {
        List<Park> parks = parkService.getParksWithLowCapacity();
        return ResponseEntity.ok(parks);
    }

    /**
     * récupère les parks entre 10000 et 40000
     * @return
     */
    @GetMapping("/medium-capacity")
    public ResponseEntity<List<ParkProjection>> getParksWithMediumCapacity() {
        List<ParkProjection> parks = parkService.getParksWithMediumCapacity();
        return ResponseEntity.ok(parks);
    }

    /**
     * ajoute une nouvelle capacité
     * 
     * @param id l'id du park a ajouté de nouvelle capacité
     * @param capacityChange les nouvelles information du capacité
     * @return une réponse HTTP 200(ok) avec la capacité ajouté
     */
    @PostMapping("/{id}/capacity-change")
    public ResponseEntity<?> addCapacityChange(@PathVariable("id") Integer id, @Valid @RequestBody ParkCapacityChange capacityChange) {
        try {
            Park updatedPark = parkService.addCapacityChange(id, capacityChange);

            updatedPark = parkService.updateCapacityWithCurrent(updatedPark);
            return ResponseEntity.ok(updatedPark);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * 
     * @param id l'identifiant du park
     * @param date la date de la capacity à récuperer
     * @return une réponse HTTP 200(ok) avec la date et la capacité cible
     */
    @GetMapping("/{id}/capacity-at-date")
    public ResponseEntity<?> getCapacityAtDate(@PathVariable("id") Integer id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        //<?> signifie que le type de la response est indéfini à l'avance ou générique
        // iso = DateTimeFormat.ISO.DATE = yyyy-MM-dd
        try {
            Integer capacity = parkService.getCapacityAtDate(id, date);

            return ResponseEntity.ok(Map.of("date", date, "capacity", capacity));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * 
     * @param id l'identifiant du park à récuperer
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/{id}/capacities-between-dates")
    public ResponseEntity<?> getCapacitiesBetweenDates(@PathVariable("id") Integer id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "startDate cannot be after endDate"));
        }
        try {
            List<Map<String, Object>> capacities = parkService.getCapacitiesGroupedByInterval(id, startDate, endDate);

            return ResponseEntity.ok(capacities);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }
}

