package com.stack.park.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stack.park.entities.Park;
import com.stack.park.exceptions.NotFoundException;
import com.stack.park.repositories.ParkRepository;

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
}
