package com.example.springcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Medicine;

@Repository
public interface MedicineRepository extends MongoRepository<Medicine, String> {

    // 1. Search by Business ID (e.g., "MED-1")
    Optional<Medicine> findByMedId(String medId);

    // 2. Search by Medicine Name (Case-Insensitive)
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // 3. Search by Company/Manufacturer
    List<Medicine> findByCompanyNameContainingIgnoreCase(String companyName);

    // 4. Find medicines by Status (e.g., "ACTIVE")
    List<Medicine> findByRecordStatus(String recordStatus);

    // 5. Search for medicines with a specific administration route (e.g., "Oral")
    List<Medicine> findByRouteIgnoreCase(String route);

    // 6. Find medicines within a certain price range
    List<Medicine> findByPriceLessThanEqual(Double price);

    // 7. Check if a Medicine ID already exists for validation
    boolean existsByMedId(String medId);

    // --- COMBINED FILTER METHODS ---

    /**
     * Combined Filter: Find by Name AND Company (Case-Insensitive)
     */
    List<Medicine> findByNameContainingIgnoreCaseAndCompanyNameContainingIgnoreCase(String name, String companyName);

    /**
     * Combined Filter: Find by Name, Company, AND Price limit
     */
    List<Medicine> findByNameContainingIgnoreCaseAndCompanyNameContainingIgnoreCaseAndPriceLessThanEqual(
            String name, String companyName, Double price);

    /**
     * Combined Filter: Find by Name AND Status
     */
    List<Medicine> findByNameContainingIgnoreCaseAndRecordStatus(String name, String recordStatus);
}