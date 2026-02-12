package com.example.springcrud.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

import com.example.springcrud.model.PatientLogin;
import com.example.springcrud.repository.PatientLoginRepository;

@RestController
@RequestMapping("/api/patient-logins")
@CrossOrigin(origins = "*")
public class PatientLoginController {

    @Autowired
    private PatientLoginRepository patientLoginRepository;

    // ================= CREATE ACCOUNT =================
    @PostMapping
    public ResponseEntity<PatientLogin> createPatientLogin(
            @RequestBody PatientLogin patientLogin) {

        patientLogin.setStatus("ACTIVE");
        patientLogin.setCreatedAt(LocalDateTime.now());
        patientLogin.setUpdatedAt(LocalDateTime.now());

        PatientLogin saved = patientLoginRepository.save(patientLogin);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ================= READ WITH FILTERS =================
    @GetMapping
    public ResponseEntity<List<PatientLogin>> getAllPatientLogins(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status
    ) {

        List<PatientLogin> patientLogins = patientLoginRepository.findAll();

        List<PatientLogin> filtered = patientLogins.stream()
                // patientId (exact)
                .filter(p -> patientId == null ||
                        (p.getPatientId() != null &&
                         p.getPatientId().equalsIgnoreCase(patientId)))

                // username (partial)
                .filter(p -> username == null ||
                        (p.getUsername() != null &&
                         p.getUsername().toLowerCase()
                          .contains(username.toLowerCase())))

                // email (exact)
                .filter(p -> email == null ||
                        (p.getEmail() != null &&
                         p.getEmail().equalsIgnoreCase(email)))

                // status
                .filter(p -> status == null ||
                        (p.getStatus() != null &&
                         p.getStatus().equalsIgnoreCase(status)))

                .collect(Collectors.toList());

        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }

    // ================= READ BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<PatientLogin> getPatientLoginById(
            @PathVariable String id) {

        Optional<PatientLogin> patientLogin = patientLoginRepository.findById(id);

        return patientLogin.map(value ->
                new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<PatientLogin> updatePatientLogin(
            @PathVariable String id,
            @RequestBody PatientLogin patientLogin
    ) {

        Optional<PatientLogin> optional = patientLoginRepository.findById(id);

        if (optional.isPresent()) {
            PatientLogin existing = optional.get();

            existing.setUsername(patientLogin.getUsername());
            existing.setEmail(patientLogin.getEmail());
            existing.setStatus(patientLogin.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());

            PatientLogin updated = patientLoginRepository.save(existing);

            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatientLogin(
            @PathVariable String id) {

        try {
            patientLoginRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}