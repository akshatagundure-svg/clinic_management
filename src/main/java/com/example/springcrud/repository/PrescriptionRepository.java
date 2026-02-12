package com.example.springcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Prescription;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // 1. Search by Patient ID (Inside nested PatientInfo)
    List<Prescription> findByPatient_PatientId(String patientId);

    // 2. Search by Doctor's Full Name (Inside nested CurrentDoctor)
    List<Prescription> findByCurrentDoctor_FullNameContainingIgnoreCase(String doctorName);

    // 3. Search by Confirmed Diagnosis (Case-Insensitive)
    List<Prescription> findByDiagnosis_ConfirmedDiagnosisContainingIgnoreCase(String diagnosis);

    // 4. Find by Record Status (e.g., "ACTIVE", "COMPLETED")
    List<Prescription> findByRecordStatusIgnoreCase(String recordStatus);

    // 5. Search for prescriptions containing a specific medicine
    List<Prescription> findByMedications_MedicineNameContainingIgnoreCase(String medicineName);

    // --- COMBINED FILTER METHODS ---

    /**
     * Filter by Patient ID AND Status
     */
    List<Prescription> findByPatient_PatientIdAndRecordStatusIgnoreCase(String patientId, String status);

    /**
     * Filter by Doctor Name AND Diagnosis
     */
    List<Prescription> findByCurrentDoctor_FullNameContainingIgnoreCaseAndDiagnosis_ConfirmedDiagnosisContainingIgnoreCase(
            String doctorName, String diagnosis);

    /**
     * Multi-Filter: Patient ID, Doctor Name, and Status
     */
    List<Prescription> findByPatient_PatientIdAndCurrentDoctor_FullNameContainingIgnoreCaseAndRecordStatusIgnoreCase(
            String patientId, String doctorName, String status);

    // 6. Check if exists
    boolean existsByPatient_PatientId(String patientId);
}