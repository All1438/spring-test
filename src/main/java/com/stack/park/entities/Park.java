package com.stack.park.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "park")
@EntityListeners(AuditingEntityListener.class)
public class Park {

    @Id
    @Column(name = "parkId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parkId;

    @Column(name= "parkName")
    private String parkName;

    @CreatedDate
    @Column(name ="createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "lastModifiedDate", nullable = false)
    private LocalDateTime lastModifiedDate;

    
    public Integer getId() {
        return parkId;
    }

    public void setId(Integer parkId) {
        this.parkId = parkId;
    }

    public String getName() {
        return parkName;
    }

    public void setName(String parkName) {
        this.parkName = parkName;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
