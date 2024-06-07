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

    // create User Method
    public Park create(Park park) {
        if (park.getOccupiedSpace() > park.getCapacity()) {
            throw new IllegalArgumentException("Occupied space cannot exceed capacity");
        }

        Optional<Park>existingPark = parkRepository.findByParkName(park.getParkName());
        if(existingPark.isPresent()) {
            throw new IllegalArgumentException("Park with the same name already exists");
        }

        return parkRepository.save(park);
    }

    // get all
    public List<Park> findAll() {
        return parkRepository.findAll();
    }

    // get by id
    public Optional<Park> findById(Integer id) {
       return parkRepository.findById(id);
    }

    // update user by id
    public Park updateById(Park user, Integer id) {
        if (!parkRepository.existsById(id)) {
            throw new NotFoundException("Park not found with id: " +id);
        }
        user.setParkId(id);
        return parkRepository.save(user);
    }

    // delete by id
    public void deleteById(Integer id) {
        if (!parkRepository.existsById(id)) {
            throw new NotFoundException("Park not found with id: " + id);
        }
        parkRepository.deleteById(id);
    }
}
