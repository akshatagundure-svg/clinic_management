package com.example.springcrud.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Doctor;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {

    // --- Standard Query Methods ---

    // Search by Specialization (Exact match, Case-Insensitive)
    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    // Search by Name (Partial match, Case-Insensitive)
    List<Doctor> findByNameContainingIgnoreCase(String name);

    // Search for a specific qualification within the List<String>
    List<Doctor> findByQualificationContainingIgnoreCase(String qualification);

    // Find Doctors with experience greater than or equal to a value
    List<Doctor> findByExperienceGreaterThanEqual(Integer experience);

    // Filter by Gender (Exact match)
    List<Doctor> findByGenderIgnoreCase(String gender);

    // Search by Phone Number
    List<Doctor> findByPhone(String phone);

    // --- Combined Filter Support ---
    
    /**
     * This method allows you to filter by Name, Specialization, AND Experience simultaneously.
     * Use this in your Controller to handle complex "Filter Sidebar" logic.
     */
    List<Doctor> findByNameContainingIgnoreCaseAndSpecializationIgnoreCaseAndExperienceGreaterThanEqual(
            String name, String specialization, Integer experience);
}