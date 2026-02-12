package com.example.springcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.DoctorLogin;

@Repository
public interface DoctorLoginRepository extends MongoRepository<DoctorLogin, String> {

    // =========================
    // BASIC FINDERS
    // =========================

    Optional<DoctorLogin> findByUsername(String username);

    Optional<DoctorLogin> findByEmail(String email);

    Optional<DoctorLogin> findByDoctorId(String doctorId);

    // =========================
    // STATUS FILTERS
    // =========================

    List<DoctorLogin> findByStatus(String status);

    // =========================
    // SEARCH FILTERS
    // =========================

    List<DoctorLogin> findByUsernameContainingIgnoreCase(String username);

    List<DoctorLogin> findByEmailContainingIgnoreCase(String email);

    // =========================
    // EXIST CHECKS (FAST)
    // =========================

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // =========================
    // DELETE
    // =========================

    void deleteByUsername(String username);
}
