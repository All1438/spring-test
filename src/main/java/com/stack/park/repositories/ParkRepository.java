package com.stack.park.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stack.park.dto.ParkProjection;
import com.stack.park.entities.Park;

@Repository
public interface ParkRepository extends JpaRepository<Park, Integer>{
    Optional<Park> findByParkName(String parkName);

    List<Park> findByCapacityGreaterThan(Integer capacity);

    @Query("SELECT p FROM Park p WHERE p.capacity < 10000")
    List<Park> findByCapacityLessThan();

    @Query("SELECT p.parkName AS parkName, p.capacity AS capacity FROM Park p WHERE p.capacity BETWEEN 10000 AND 40000")
    List<ParkProjection> findParksWithMediumCapacity();
}
