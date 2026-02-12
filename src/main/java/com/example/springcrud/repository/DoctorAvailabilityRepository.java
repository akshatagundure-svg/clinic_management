package com.example.springcrud.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.DoctorAvailability;

@Repository
public interface DoctorAvailabilityRepository extends MongoRepository<DoctorAvailability, String> {

    // 1. Find all availability slots for a specific Doctor
    List<DoctorAvailability> findByDoctorId(String doctorId);

    // 2. Find availability by Day (e.g., "Thursday")
    List<DoctorAvailability> findByDayIgnoreCase(String day);

    // 3. Find only active availability slots
    List<DoctorAvailability> findByAvailableTrue();

    // 4. Find slots starting at a specific time
    List<DoctorAvailability> findByStartTime(String startTime);

    // 5. Check if a doctor has any records
    boolean existsByDoctorId(String doctorId);

    // --- COMBINED FILTER METHODS ---

    /**
     * Filter by Doctor ID and Day
     */
    List<DoctorAvailability> findByDoctorIdAndDayIgnoreCase(String doctorId, String day);

    /**
     * Filter by Day and Availability Status
     * Use Case: Find all doctors available on "Monday"
     */
    List<DoctorAvailability> findByDayIgnoreCaseAndAvailable(String day, Boolean available);

    /**
     * Complete Filter: Doctor, Day, and Availability Status
     * Use Case: Check if Doctor X is free on Monday
     */
    List<DoctorAvailability> findByDoctorIdAndDayIgnoreCaseAndAvailable(String doctorId, String day, Boolean available);

    /**
     * Find available slots for a specific doctor on a specific day (Strictly True)
     */
    List<DoctorAvailability> findByDoctorIdAndDayIgnoreCaseAndAvailableTrue(String doctorId, String day);
}