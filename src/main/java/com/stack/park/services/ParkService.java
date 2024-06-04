package com.stack.park.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stack.park.entities.Park;
import com.stack.park.repositories.ParkRepository;

@Service
public class ParkService {

    @Autowired
    private ParkRepository userRepository;

    // create User Method
    public Park create(Park user) {
        return userRepository.save(user);
    }

    // get all
    public List<Park> findAll() {
        return userRepository.findAll();
    }

    // get by id
    public Optional<Park> findById(Integer id) {
       return userRepository.findById(id);
    }

    // update user by id
    public Park updateById(Park user, Integer id) {
        user.setId(id);
        return userRepository.save(user);
    }

    // delete by id
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}
