package com.example.springcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.PatientLogin;

@Repository
public interface PatientLoginRepository extends MongoRepository<PatientLogin, String> {

    // =========================
    // BASIC FINDERS
    // =========================

    /**
     * Find patient login by unique username.
     */
    Optional<PatientLogin> findByUsername(String username);

    /**
     * Find patient login by unique email.
     */
    Optional<PatientLogin> findByEmail(String email);

    /**
     * Find patient login by the linked Patient Business ID (e.g., PAT-602).
     */
    Optional<PatientLogin> findByPatientId(String patientId);

    // =========================
    // STATUS FILTERS
    // =========================

    /**
     * List logins by account status (ACTIVE, INACTIVE, BLOCKED).
     */
    List<PatientLogin> findByStatus(String status);

    // =========================
    // SEARCH FILTERS (Combined)
    // =========================

    /**
     * Search by username with partial matches (Case-Insensitive).
     */
    List<PatientLogin> findByUsernameContainingIgnoreCase(String username);

    /**
     * Search by email with partial matches (Case-Insensitive).
     */
    List<PatientLogin> findByEmailContainingIgnoreCase(String email);

    // =========================
    // EXIST CHECKS (Fast validation)
    // =========================

    /**
     * Check if a username is already taken.
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a specific patient already has a login account.
     */
    boolean existsByPatientId(String patientId);

    // =========================
    // DELETE
    // =========================

    /**
     * Remove a login record by username.
     */
    void deleteByUsername(String username);
}