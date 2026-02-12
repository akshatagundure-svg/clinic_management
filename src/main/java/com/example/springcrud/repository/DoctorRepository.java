package com.example.springcrud.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Doctor;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {

    // --- Original Query Methods ---

    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    List<Doctor> findByNameContainingIgnoreCase(String name);

    List<Doctor> findByQualificationContainingIgnoreCase(String qualification);

    List<Doctor> findByExperienceGreaterThanEqual(Integer experience);

    List<Doctor> findByGenderIgnoreCase(String gender);

    List<Doctor> findByPhone(String phone);

    // --- New Field Query Methods ---

    // Search by Hospital (Partial match, Case-Insensitive)
    List<Doctor> findByHospitalNameContainingIgnoreCase(String hospitalName);

    // Search by Availability (e.g., "AVAILABLE", "ON_LEAVE")
    List<Doctor> findByAvailabilityIgnoreCase(String availability);

    // Find by Email (Exact match for login or identification)
    List<Doctor> findByEmailIgnoreCase(String email);

    // Range Filter: Consultation Fee (Find doctors cheaper than or equal to a price)
    List<Doctor> findByConsultationFeeLessThanEqual(Double maxFee);

    // --- Combined Filter Support ---

    /**
     * Updated to include Hospital Name in the search logic.
     */
    List<Doctor> findByNameContainingIgnoreCaseAndSpecializationIgnoreCaseAndHospitalNameContainingIgnoreCase(
            String name, String specialization, String hospitalName);

    /**
     * Check if a phone number exists (useful for validation during registration)
     */
    boolean existsByPhone(String phone);
    
    /**
     * Check if an email exists
     */
    boolean existsByEmail(String email);
}