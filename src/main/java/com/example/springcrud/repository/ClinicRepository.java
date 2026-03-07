package com.example.springcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Clinic;

@Repository
public interface ClinicRepository extends MongoRepository<Clinic, String> {

    // Standard business ID lookups
    Optional<Clinic> findByClinicId(String clinicId);

    boolean existsByClinicId(String clinicId);

    void deleteByClinicId(String clinicId);

    /**
     * ✅ NEW: Find clinics where the top-level 'doctorId' matches.
     * This is used for ownership-based filtering (clinics created by this doctor).
     */
    List<Clinic> findByDoctorId(String doctorId);

    /**
     * ✅ Find clinics where the embedded 'doctors' list contains 
     * a doctor with the matching 'doctorId'.
     * This is useful if multiple doctors practice at the same clinic.
     */
    List<Clinic> findByDoctorsDoctorId(String doctorId);
    long countByDoctorId(String doctorId);

}