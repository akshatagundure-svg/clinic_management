package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;
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

import com.example.springcrud.model.LoginRequest;
import com.example.springcrud.model.Patient;
import com.example.springcrud.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    // =====================================================
    // 📊 METRICS & SEARCH
    // =====================================================

    @GetMapping("/count")
    public long getCount() {
        return patientRepository.count();
    }

    @GetMapping("/count/{doctorId}")
public long getPatientCount(@PathVariable String doctorId) {
    return patientRepository.countByDoctorId(doctorId);
}

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
            patients = patients.stream()
                    .filter(p -> patientId.equals(p.getPatientId()))
                    .collect(Collectors.toList());
        }

        if (name != null && !name.isEmpty()) {
            String n = name.toLowerCase();
            patients = patients.stream()
                    .filter(p -> p.getFullName() != null &&
                                 p.getFullName().toLowerCase().contains(n))
                    .collect(Collectors.toList());
        }

        if (allergy != null && !allergy.isEmpty()) {
            String a = allergy.toLowerCase();
            patients = patients.stream()
                    .filter(p -> p.getAllergies() != null &&
                            p.getAllergies().stream()
                                .anyMatch(x -> x.toLowerCase().contains(a)))
                    .collect(Collectors.toList());
        }

        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            patients = patients.stream()
                    .filter(p -> p.getBloodGroup() != null &&
                                 p.getBloodGroup().equalsIgnoreCase(bloodGroup))
                    .collect(Collectors.toList());
        }

        if (phoneContains != null && !phoneContains.isEmpty()) {
            patients = patients.stream()
                    .filter(p -> p.getPhone() != null &&
                                 p.getPhone().contains(phoneContains))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(patients);
    }

    // =====================================================
    // 📂 CRUD
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        try {
            return ResponseEntity.ok(patientRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database record error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ FIXED — this was wrong earlier
    // Return patients belonging to a doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Patient>> getPatientsByDoctor(@PathVariable String doctorId) {
        List<Patient> list = patientRepository.findByDoctorId(doctorId);
        return ResponseEntity.ok(list);
    }

    // =====================================================
    // CREATE
    // =====================================================

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

    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(@RequestBody Patient patient) {
        if (patient.getFullName() == null || patient.getPhone() == null) {
            return ResponseEntity.badRequest().build();
        }
        ensureUniquePatientId(patient);
        return new ResponseEntity<>(patientRepository.save(patient), HttpStatus.CREATED);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient d) {
        return patientRepository.findById(id)
                .map(p -> {
                    p.setPatientId(d.getPatientId());
                    p.setDoctorId(d.getDoctorId());
                    p.setFullName(d.getFullName());
                    p.setPassword(d.getPassword());
                    p.setDateOfBirth(d.getDateOfBirth());
                    p.setGender(d.getGender());
                    p.setPhone(d.getPhone());
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
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<Patient> addPatientUnderDoctor(
        @PathVariable String doctorId,
        @RequestBody Patient patient) {

    ensureUniquePatientId(patient);
    patient.setDoctorId(doctorId);   // 🔥 auto set doctor

    Patient saved = patientRepository.save(patient);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}
    // =====================================================
// 🔐 AUTHENTICATION
// =====================================================

@PostMapping("/login")
public ResponseEntity<?> authPatient(@RequestBody LoginRequest request, HttpSession session) {
    // Patients typically log in via phone and password
    // We filter the list to find the matching patient
    List<Patient> patients = patientRepository.findAll();

    Optional<Patient> patient = patients.stream()
            .filter(p -> request.getPhone().equals(p.getPhone()) && 
                         request.getPassword().equals(p.getPassword()))
            .findFirst();

    if (patient.isPresent()) {
        // ✅ Store session attributes specifically for Patients
        session.setAttribute("patientId", patient.get().getPatientId());
        session.setAttribute("patientName", patient.get().getFullName());
        session.setAttribute("role", "PATIENT");

        return new ResponseEntity<>(patient.get(), HttpStatus.OK);
    } else {
        return new ResponseEntity<>("Invalid phone or password", HttpStatus.UNAUTHORIZED);
    }
}

@PostMapping("/logout")
public ResponseEntity<String> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.ok("Logged out successfully");
}

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable String id) {
        if (!patientRepository.existsById(id))
            return ResponseEntity.notFound().build();

        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void ensureUniquePatientId(Patient patient) {
        if (patient.getPatientId() == null || patient.getPatientId().trim().isEmpty()) {
            String uniqueId = "PAT-" +
                    UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            patient.setPatientId(uniqueId);
        }
    }


}
