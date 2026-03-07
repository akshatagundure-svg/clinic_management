package com.example.springcrud.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.springcrud.model.Prescription;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // --- RELATIONAL LOOKUPS ---

    /**
     * Finds prescriptions by the patientId nested inside the PatientInfo object.
     * Required for: GET /api/prescriptions/patient/{patientId}
     */
   

    /**
     * Finds prescriptions by the doctorId nested inside the CurrentDoctor object.
     * Required for: GET /api/prescriptions/doctor/{doctorId}
     */
    List<Prescription> findByCurrentDoctor_DoctorId(String doctorId);

    // --- SEARCH & FILTERING ---

    /**
     * Search by Doctor's Full Name inside nested CurrentDoctor (Case-Insensitive).
     */
    List<Prescription> findByCurrentDoctor_FullNameContainingIgnoreCase(String doctorName);

    /**
     * Search by Confirmed Diagnosis inside nested Diagnosis object (Case-Insensitive).
     */
    List<Prescription> findByDiagnosis_ConfirmedDiagnosisContainingIgnoreCase(String diagnosis);

    /**
     * Find by Record Status (e.g., "ACTIVE", "DRAFT", "COMPLETED").
     */
    List<Prescription> findByRecordStatusIgnoreCase(String recordStatus);

    /**
     * Search for prescriptions containing a specific medicine name.
     */
    List<Prescription> findByMedications_MedicineNameContainingIgnoreCase(String medicineName);

    // --- COMBINED FILTER METHODS ---

    /**
     * Filter by Patient ID AND Status.
     */
    List<Prescription> findByPatient_PatientIdAndRecordStatusIgnoreCase(String patientId, String status);

    /**
     * Filter by Doctor Name AND Diagnosis.
     */
    List<Prescription> findByCurrentDoctor_FullNameContainingIgnoreCaseAndDiagnosis_ConfirmedDiagnosisContainingIgnoreCase(
            String doctorName, String diagnosis);

    /**
     * Multi-Filter: Patient ID, Doctor Name, and Status.
     */
    List<Prescription> findByPatient_PatientIdAndCurrentDoctor_FullNameContainingIgnoreCaseAndRecordStatusIgnoreCase(
            String patientId, String doctorName, String status);

    // --- HELPERS ---

    /**
     * Check if a patient already has records in the system.
     */
    boolean existsByPatient_PatientId(String patientId);
    List<Prescription> findByPatient_PatientId(String patientId);
}