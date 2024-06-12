package com.stack.park.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "park_capacity_change")
public class ParkCapacityChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parkId", nullable=false)
    @JsonBackReference
    private Park park;

    @Column(name = "newCapacity", nullable=false)
    @Min(value=1, message= "Capacity must be at least 1")
    @NotNull(message = "newCapacity is mandatory")
    private Integer newCapacity;

    @Column(name="startDate", nullable=false)
    @NotNull(message = "startDate is mandatory")
    private LocalDate startDate;

    @Column(name="endDate")
    private LocalDate endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Park getPark() {
        return park;
    }

    public void setPark(Park park) {
        this.park = park;
    }

    public Integer getNewCapacity() {
        return newCapacity;
    }

    public void setNewCapacity(Integer newCapacity) {
        this.newCapacity = newCapacity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
