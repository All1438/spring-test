package com.stack.park.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stack.park.dto.ParkProjection;
import com.stack.park.entities.Park;
import com.stack.park.entities.ParkCapacityChange;
import com.stack.park.exceptions.NotFoundException;
import com.stack.park.repositories.ParkRepository;

import jakarta.transaction.Transactional;

@Service
public class ParkService {

    @Autowired
    private ParkRepository parkRepository;


    private void validatePark(Park park) { // validation centralisé
        if (park.getOccupiedSpace() > park.getCapacity()) {
            throw new IllegalArgumentException("Occupied space cannot exceed capacity");
        }

        if (park.getOccupiedSpace() < 0) {
            throw new IllegalArgumentException("Occupied space cannot be negative");
        }

        if (park.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        Optional<Park> existingPark = parkRepository.findByParkName(park.getParkName());
        if (existingPark.isPresent()) {
            throw new IllegalArgumentException("Park with the same name already exists");
        }
    }

    // create User Method
    public Park create(Park park) {
        validatePark(park);
        return parkRepository.save(park);
    }

    // get all
    public List<Park> findAll() {
        return parkRepository.findAll();
    }

    // get by parkId
    public Optional<Park> findById(Integer parkId) {
        if(!parkRepository.existsById(parkId)) {
            throw new NotFoundException("Park not found with parkId: " +parkId);
        }
       return parkRepository.findById(parkId);
    }

    // update park by parkId
    public Park updateById(Park park, Integer parkId) {
        if (!parkRepository.existsById(parkId)) {
            throw new NotFoundException("Park not found with parkId: " +parkId);
        }
        validatePark(park);
        park.setParkId(parkId);
        return parkRepository.save(park);
    }

    // delete by parkId
    public void deleteById(Integer parkId) {
        if (!parkRepository.existsById(parkId)) {
            throw new NotFoundException("Park not found with parkId: " + parkId);
        }
        parkRepository.deleteById(parkId);
    }

    // get park with capacity Greater than 10000
    public List<Park> getCapacityGreaterThan10000() {
        return parkRepository.findByCapacityGreaterThan(10000);
    }

    // get park with capacity less than 10000
    public List<Park> getParksWithLowCapacity() {
        return parkRepository.findByCapacityLessThan();
    }

    // get park with capacity enter 10000 and 40000
    public List <ParkProjection> getParksWithMediumCapacity() {
        return parkRepository.findParksWithMediumCapacity();
    }

    @Transactional // rollback (complètement annulée en cas d'erreur)
    public Park addCapacityChange(Integer parkId, ParkCapacityChange newChange) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new NotFoundException("Park not found with id: " + parkId));

        // obtenir la list des changements de capacité existants pour ce park(parkId)
        List<ParkCapacityChange> changes = park.getCapacityChanges();

        for (ParkCapacityChange existingChange : changes) {
            LocalDate existingStart = existingChange.getStartDate();
            LocalDate existingEnd = existingChange.getEndDate() != null ? existingChange.getEndDate() : LocalDate.MAX;

            if (!newChange.getEndDate().isBefore(existingStart) && !newChange.getStartDate().isAfter(existingEnd)) {
                // intervalles qui se chevauchent trouvées

                // Si la date de début du nouveau changement est après la date de début de l'existant
                if(newChange.getStartDate().isAfter(existingStart)) {
                    // créer un newChange avant la nouvelle interval

                    ParkCapacityChange beforeChange = new ParkCapacityChange();
                    
                    beforeChange.setPark(park);
                    beforeChange.setNewCapacity(existingChange.getNewCapacity());
                    beforeChange.setStartDate(existingStart);
                    beforeChange.setEndDate(newChange.getStartDate().minusDays(1)); //.minusDays(1) = la date sera moins de un jour

                    park.getCapacityChanges().add(beforeChange); // ajouté beforeChange à la liste des changements de capacité du park
                }

                if(newChange.getEndDate().isBefore(existingEnd)) {
                    // créer un newChange après la nouvelle interval

                    ParkCapacityChange afterChange = new ParkCapacityChange();
                    afterChange.setPark(park);
                    afterChange.setNewCapacity(existingChange.getNewCapacity());
                    afterChange.setStartDate(newChange.getEndDate().plusDays(1));
                    afterChange.setEndDate(existingEnd);
                    park.getCapacityChanges().add(afterChange);
                }

                // supprime l'ancien changement qui se chevauche
                park.getCapacityChanges().remove(existingChange);
                break; // quité la boucle parcequ'on a modifié la liste
            }
        }
        // Add the new change
        newChange.setPark(park);
        park.getCapacityChanges().add(newChange);

        // trié les modif par date de début pour plus de cohérence
        park.getCapacityChanges().sort(Comparator.comparing(ParkCapacityChange::getStartDate));
        Park updatedPark = parkRepository.save(park);
        return updateCapacityWithCurrent(updatedPark);
    }

    public Integer getCurrentCapacity(Park park) {
        // récupère la date actuel
        LocalDate today = LocalDate.now();

        // if(capacityChanges == null) {
        //     return capacity;
        // }

        Optional<ParkCapacityChange> currentChange = park.getCapacityChanges().stream()
            .filter(change -> (change.getStartDate().isBefore(today) || change.getStartDate().isEqual(today)) && (change.getEndDate() == null || change.getEndDate().isAfter(today) || change.getEndDate().isEqual(today)))
            // Vérifie que la date de début du changement de capacité (startDate) est antérieure ou égale à la date actuelle (today)
            .findFirst(); // recherche et renvoi le premier changement de capacité correspondant aux critères. si il y a pas de changement l'Optional sera vide

            return currentChange.map(ParkCapacityChange::getNewCapacity).orElse(park.getCapacity());
    }

    public Park updateCapacityWithCurrent(Park park) {
        Integer currentCapacity = getCurrentCapacity(park);
        park.setCapacity(currentCapacity);
        return parkRepository.save(park);
    }

    // Get capacity at Date
    public Integer getCapacityAtDate(Integer parkId, LocalDate date) {
        Park park = parkRepository.findById(parkId).orElseThrow(() -> new NotFoundException("Park not found with id: " + parkId));

        Optional<ParkCapacityChange> capacityChangeAtDate = park.getCapacityChanges().stream() //.stream() = utilisé pour transformer les listes en un flux de données, ce qu'on permet d'effectuer des opérations de filtrage
            .filter(change -> 
                (change.getStartDate().isBefore(date) || change.getStartDate().isEqual(date)) && (change.getEndDate() == null || change.getEndDate().isAfter(date) || change.getEndDate().isEqual(date))
            )
            .findFirst();

        return capacityChangeAtDate.map(ParkCapacityChange::getNewCapacity).orElse(park.getCapacity());
    }

    // Get Capacity at interval Date
    public List<Map<String, Object>> getCapacityAtBetweenDates(Integer parkId, LocalDate startDate, LocalDate endDate) {
        Park park = parkRepository.findById(parkId).orElseThrow(() -> new NotFoundException("Park not found with id: " + parkId));

        List<Map<String, Object>> capacities = new ArrayList<>();
        LocalDate currentDate = startDate;

        while(!currentDate.isAfter(endDate)) {
            Integer capacity = getCapacityAtDate(parkId, currentDate);
            Map<String, Object> capacityEntry = new HashMap<>();
            capacityEntry.put("date", currentDate);
            capacityEntry.put("capacity", capacity);
            capacities.add(capacityEntry);
            currentDate = currentDate.plusDays(1);
        }

        return capacities;
    }

    // Get Capacity at interval Date
    public List<Map<String, Object>> getCapacitiesGroupedByInterval(Integer parkId, LocalDate startDate, LocalDate endDate) {
        Park park = parkRepository.findById(parkId).orElseThrow(() -> new NotFoundException("Park not found with id: " + parkId));

        List<Map<String, Object>> capacities = new ArrayList<>();
        LocalDate currentDate = startDate;
        Integer currentCapacity = null;
        LocalDate intervalStartDate = startDate;

        // tant que la date actuel ne dépasse pas le le endDate
        while(!currentDate.isAfter(endDate)) {
            Integer capacityAtDate = getCapacityAtDate(parkId, currentDate);

            if (currentCapacity == null) {
                currentCapacity = capacityAtDate;
            }

            // si la capacity de la date n'est pas égale à la capacity actuel ou la date actuel est égale endDate 
            if (!capacityAtDate.equals(currentCapacity)) {
                Map<String, Object> capacityEntry = new HashMap<>();
                capacityEntry.put("capacity", currentCapacity);
                capacityEntry.put("endDate", currentDate.equals(endDate) ? endDate : currentDate.minusDays(1));
                capacityEntry.put("startDate", intervalStartDate);

                capacities.add(capacityEntry);

                intervalStartDate = currentDate;
                currentCapacity = capacityAtDate;
            }
            currentDate = currentDate.plusDays(1);

        }
                if(!currentDate.equals(intervalStartDate)) {
                    Map<String, Object> capacityEntry = new HashMap<>();
                    capacityEntry.put("capacity", currentCapacity);
                    capacityEntry.put("startDate", intervalStartDate);
                    capacityEntry.put("endDate", endDate);
    
                    capacities.add(capacityEntry);
                }

        return capacities;
    }
}

