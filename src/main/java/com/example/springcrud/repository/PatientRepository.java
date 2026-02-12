package com.example.springcrud.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    // =====================================================
    // ✅ BASIC SEARCH
    // =====================================================

    Optional<Patient> findByPatientId(String patientId);

    Optional<Patient> findByEmailAddress(String emailAddress);

    List<Patient> findByFullNameContainingIgnoreCase(String fullName);

    List<Patient> findByBloodGroupIgnoreCase(String bloodGroup);

    List<Patient> findByGenderIgnoreCase(String gender);

    List<Patient> findByPhoneNumber(String phoneNumber);

    List<Patient> findByPhoneNumberContaining(String digits);


    // =====================================================
    // ✅ LIST FIELD SEARCH
    // =====================================================

    List<Patient> findByAllergiesContainingIgnoreCase(String allergy);

    List<Patient> findByChronicDiseasesContainingIgnoreCase(String disease);

    List<Patient> findByCurrentMedicationsContainingIgnoreCase(String med);


    // =====================================================
    // ✅ DATE SEARCH
    // =====================================================

    List<Patient> findByDateOfBirth(LocalDate dateOfBirth);

    List<Patient> findByDateOfBirthBetween(LocalDate from, LocalDate to);


    // =====================================================
    // ✅ RANGE SEARCH (Numbers)
    // =====================================================

    List<Patient> findByHeightBetween(Double min, Double max);

    List<Patient> findByWeightBetween(Double min, Double max);


    // =====================================================
    // ✅ LOGIN SUPPORT
    // =====================================================

    Optional<Patient> findByPatientIdAndDateOfBirthAndPhoneNumber(
            String patientId,
            LocalDate dateOfBirth,
            String phoneNumber
    );

    Optional<Patient> findByEmailAddressAndPhoneNumber(
            String emailAddress,
            String phoneNumber
    );


    // =====================================================
    // ✅ EXISTS CHECKS
    // =====================================================

    boolean existsByPatientId(String patientId);

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByPhoneNumber(String phoneNumber);


    // =====================================================
    // ✅ COMBINED FILTERS (DB LEVEL — FAST)
    // =====================================================

    List<Patient> findByFullNameContainingIgnoreCaseAndAllergiesContainingIgnoreCase(
            String name,
            String allergy
    );

    List<Patient> findByFullNameContainingIgnoreCaseAndBloodGroupIgnoreCase(
            String name,
            String bloodGroup
    );

    List<Patient>
    findByFullNameContainingIgnoreCaseAndAllergiesContainingIgnoreCaseAndBloodGroupIgnoreCase(
            String name,
            String allergy,
            String bloodGroup
    );

    List<Patient>
    findByGenderIgnoreCaseAndBloodGroupIgnoreCase(
            String gender,
            String bloodGroup
    );


    // =====================================================
    // ✅ CONTACT SEARCH
    // =====================================================

    List<Patient> findByResidentialAddressContainingIgnoreCase(String city);

    List<Patient> findByEmergencyContactContainingIgnoreCase(String contact);

}
