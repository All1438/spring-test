package com.stack.park.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "park")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Park {

    @Id
    @Column(name = "parkId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parkId;

    @Column(name= "parkName", nullable=false)
    @NotBlank(message = "Park name is mandatory") // @NotBlank() = pour les types String
    private String parkName;

    
    @Column(name= "capacity", nullable=false)
    @Min(value=1, message = "Capacity must be at least 1")
    @NotNull(message = "Capacity is mandatory") // @NotNull() = pour les types Integer
    private Integer capacity;

    @Column(name= "occupiedSpace", nullable=false)
    @Min(value=0, message = "Occupied space cannot be negative")
    @NotNull(message = "occupid space is Mandatory")
    private Integer occupiedSpace;

    @CreatedDate
    @Column(name ="createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "lastModifiedDate", nullable = false)
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "park", cascade = CascadeType.ALL, orphanRemoval = true) // CascadeType.ALL = Applique toutes les opérations de cascade: PERSIST, MERGE, REMOVE, REFRESH, DETACH
    // orphanRemoval=true = 
    @JsonBackReference
    private List<ParkCapacityChange> capacityChanges = new ArrayList<>();


    
    
    
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getOccupiedSpace() {
        return occupiedSpace;
    }
    
    public void setOccupiedSpace(Integer occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }

    public Integer getParkId() {
        return parkId;
    }

    public void setParkId(Integer parkId) {
        this.parkId = parkId;
    }
    
    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
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
    
    public List<ParkCapacityChange> getCapacityChanges() {
        return capacityChanges;
    }

    public void setCapacityChanges(List<ParkCapacityChange> capacityChanges) {
        this.capacityChanges = capacityChanges;
    }

    
    

    
}
