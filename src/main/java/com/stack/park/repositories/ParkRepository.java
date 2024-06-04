package com.stack.park.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stack.park.entities.Park;

@Repository
public interface ParkRepository extends JpaRepository<Park, Integer>{

}
