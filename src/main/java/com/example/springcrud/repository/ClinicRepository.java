package com.example.springcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Clinic;

@Repository
public interface ClinicRepository extends MongoRepository<Clinic, String> {

    // 1. Search by Clinic Name (Partial match, Case-Insensitive)
    List<Clinic> findByClinicNameContainingIgnoreCase(String clinicName);

    // 2. Search by Clinic Type (e.g., "Dental", "General")
    List<Clinic> findByClinicTypeIgnoreCase(String clinicType);

    // 3. Search by Registration Number (Exact match)
    Optional<Clinic> findByRegistrationNumber(String registrationNumber);

    // 4. Find all clinics by Status (e.g., "Active")
    List<Clinic> findByStatusIgnoreCase(String status);

    // 5. Search for a specific service inside the services List<String>
    List<Clinic> findByServicesContainingIgnoreCase(String service);

    // --- COMBINED FILTER METHODS ---

    /**
     * Filter by Name AND Type
     */
    List<Clinic> findByClinicNameContainingIgnoreCaseAndClinicTypeIgnoreCase(String name, String type);

    /**
     * Filter by Type AND Status
     * Use Case: Find all "Active" "Pediatric" clinics.
     */
    List<Clinic> findByClinicTypeIgnoreCaseAndStatusIgnoreCase(String type, String status);

    /**
     * Comprehensive Filter: Name, Type, and Status
     */
    List<Clinic> findByClinicNameContainingIgnoreCaseAndClinicTypeIgnoreCaseAndStatusIgnoreCase(
            String name, String type, String status);

    // 6. Check if a registration number already exists
    boolean existsByRegistrationNumber(String registrationNumber);
}