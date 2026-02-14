package com.example.springcrud.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springcrud.model.Patient;
import com.example.springcrud.repository.PatientRepository;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    // =====================================================
    // 📊 METRICS & SEARCH (Specific paths first)
    // =====================================================

    /**
     * Get total count of patients for dashboard stats
     */
    @GetMapping("/count")
    public long getCount() {
        return patientRepository.count();
    }

    /**
     * Advanced Search/Filtering
     * Usage: /api/patients/search?name=John&bloodGroup=O+
     */
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> filterPatients(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String allergy,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String phoneContains
    ) {
        List<Patient> patients = patientRepository.findAll();

        if (patientId != null && !patientId.isEmpty()) {
            patients = patients.stream().filter(p -> patientId.equals(p.getPatientId())).collect(Collectors.toList());
        }
        if (name != null && !name.isEmpty()) {
            String n = name.toLowerCase();
            patients = patients.stream().filter(p -> p.getFullName().toLowerCase().contains(n)).collect(Collectors.toList());
        }
        if (allergy != null && !allergy.isEmpty()) {
            String a = allergy.toLowerCase();
            patients = patients.stream().filter(p -> p.getAllergies().stream().anyMatch(x -> x.toLowerCase().contains(a))).collect(Collectors.toList());
        }
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            patients = patients.stream().filter(p -> p.getBloodGroup() != null && p.getBloodGroup().equalsIgnoreCase(bloodGroup)).collect(Collectors.toList());
        }
        if (phoneContains != null && !phoneContains.isEmpty()) {
            patients = patients.stream().filter(p -> p.getPhoneNumber() != null && p.getPhoneNumber().contains(phoneContains)).collect(Collectors.toList());
        }
        return ResponseEntity.ok(patients);
    }

    // =====================================================
    // 📂 CRUD OPERATIONS
    // =====================================================

    /**
     * Fetch all patients
     */
    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        try {
            return ResponseEntity.ok(patientRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database record error: " + e.getMessage());
        }
    }

    /**
     * Fetch single patient by MongoDB ID
     * (Placed after /count and /search to avoid path conflict)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * CREATE: Full Form Submission
     * Handles ID generation to prevent Duplicate Key errors
     */
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        try {
            ensureUniquePatientId(patient);
            Patient saved = patientRepository.save(patient);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * CREATE: Self Registration
     */
    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(@RequestBody Patient patient) {
        if (patient.getFullName() == null || patient.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().build();
        }
        ensureUniquePatientId(patient);
        return new ResponseEntity<>(patientRepository.save(patient), HttpStatus.CREATED);
    }

    /**
     * UPDATE: Modify existing patient
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient d) {
        return patientRepository.findById(id)
            .map(p -> {
                p.setPatientId(d.getPatientId());
                p.setFullName(d.getFullName());
                p.setDateOfBirth(d.getDateOfBirth());
                p.setGender(d.getGender());
                p.setPhoneNumber(d.getPhoneNumber());
                p.setEmailAddress(d.getEmailAddress());
                p.setResidentialAddress(d.getResidentialAddress());
                p.setEmergencyContact(d.getEmergencyContact());
                p.setBloodGroup(d.getBloodGroup());
                p.setAllergies(d.getAllergies());
                p.setChronicDiseases(d.getChronicDiseases());
                p.setCurrentMedications(d.getCurrentMedications());
                p.setHeight(d.getHeight());
                p.setWeight(d.getWeight());
                return ResponseEntity.ok(patientRepository.save(p));
            }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE: Remove patient
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable String id) {
        if (!patientRepository.existsById(id)) return ResponseEntity.notFound().build();
        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // 🛠 PRIVATE HELPERS
    // =====================================================

    /**
     * Helper to ensure every saved patient has a generated Patient ID
     */
    private void ensureUniquePatientId(Patient patient) {
        if (patient.getPatientId() == null || patient.getPatientId().trim().isEmpty()) {
            String uniqueId = "PAT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            patient.setPatientId(uniqueId);
        }
    }
}