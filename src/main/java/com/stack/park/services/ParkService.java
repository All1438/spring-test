package com.stack.park.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stack.park.dto.ParkProjection;
import com.stack.park.entities.Park;
import com.stack.park.entities.ParkCapacityChange;
import com.stack.park.exceptions.NotFoundException;
// import com.stack.park.repositories.ParkCapacityChangeRepository;
import com.stack.park.repositories.ParkRepository;

@Service
public class ParkService {

    @Autowired
    private ParkRepository parkRepository;

    // @Autowired
    // private ParkCapacityChangeRepository capacityChangeRepository;

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

    public List<Park> getParksWithLowCapacity() {
        return parkRepository.findByCapacityLessThan();
    }

    public List <ParkProjection> getParksWithMediumCapacity() {
        return parkRepository.findParksWithMediumCapacity();
    }

    public Park addCapacityChange(Integer parkId, ParkCapacityChange capacityChange) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new NotFoundException("Park not found with id: " + parkId));

        capacityChange.setPark(park);
        park.getCapacityChanges().add(capacityChange);
        return parkRepository.save(park);
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

    public Integer getCapacityAtDate(Integer parkId, LocalDate date) {
        Park park = parkRepository.findById(parkId).orElseThrow(() -> new NotFoundException("Park not found with id: " + parkId));

        Optional<ParkCapacityChange> capacityChangeAtDate = park.getCapacityChanges().stream()
            .filter(change -> 
                (change.getStartDate().isBefore(date) || change.getStartDate().isEqual(date)) && (change.getEndDate() == null || change.getEndDate().isAfter(date) || change.getEndDate().isEqual(date))
            )
            .findFirst();

        return capacityChangeAtDate.map(ParkCapacityChange::getNewCapacity).orElse(park.getCapacity());
    }
}

