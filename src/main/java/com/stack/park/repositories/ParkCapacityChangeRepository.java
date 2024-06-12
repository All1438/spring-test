package com.stack.park.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stack.park.entities.ParkCapacityChange;

@Repository
public interface ParkCapacityChangeRepository extends JpaRepository<ParkCapacityChange, Integer> {

}
