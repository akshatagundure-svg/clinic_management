package com.example.springcrud.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.MedicalTest;

@Repository
public interface MedicalTestRepository extends MongoRepository<MedicalTest, String> {

    // 1. Find all tests for a specific Patient (using Business ID)
    List<MedicalTest> findByPatientId(String patientId);

    // 2. Find all tests ordered by a specific Doctor
    List<MedicalTest> findByDoctorId(String doctorId);

    // 3. Search by Test Name (e.g., "HbA1c") - Case-Insensitive
    List<MedicalTest> findByTestNameContainingIgnoreCase(String testName);

    // 4. Search by Category (e.g., "Diabetology")
    List<MedicalTest> findByCategoryIgnoreCase(String category);

    // 5. Find tests by Result Status (e.g., "Completed" or "Pending")
    List<MedicalTest> findByResultStatusIgnoreCase(String resultStatus);

    // 6. Find tests within a budget (Price filter)
    List<MedicalTest> findByPriceLessThanEqual(Double price);

    // 7. Check if any tests exist for a patient
    boolean existsByPatientId(String patientId);

    // --- COMBINED FILTER METHODS ---

    /**
     * Filter by Patient ID AND Result Status
     * Use case: Finding all "Pending" tests for a specific patient.
     */
    List<MedicalTest> findByPatientIdAndResultStatusIgnoreCase(String patientId, String resultStatus);

    /**
     * Filter by Test Name AND Category
     * Use case: Searching for "Blood" tests specifically in the "Hematology" category.
     */
    List<MedicalTest> findByTestNameContainingIgnoreCaseAndCategoryIgnoreCase(String testName, String category);

    /**
     * Comprehensive Filter: Patient, Status, and Category
     */
    List<MedicalTest> findByPatientIdAndResultStatusIgnoreCaseAndCategoryIgnoreCase(
            String patientId, String resultStatus, String category);

    /**
     * Filter by Doctor AND Status
     */
    List<MedicalTest> findByDoctorIdAndResultStatusIgnoreCase(String doctorId, String resultStatus);
}