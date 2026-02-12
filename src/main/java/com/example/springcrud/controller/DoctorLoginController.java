package com.example.springcrud.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.DoctorLogin;
import com.example.springcrud.repository.DoctorLoginRepository;

@RestController
@RequestMapping("/api/doctor-logins")
public class DoctorLoginController {

    @Autowired
    private DoctorLoginRepository doctorLoginRepository;

    // ================= CREATE ACCOUNT =================
    @PostMapping
    public ResponseEntity<DoctorLogin> createDoctorLogin(
            @RequestBody DoctorLogin doctorLogin) {

        doctorLogin.setStatus("ACTIVE");
        doctorLogin.setCreatedAt(LocalDateTime.now());
        doctorLogin.setUpdatedAt(LocalDateTime.now());

        DoctorLogin saved =
                doctorLoginRepository.save(doctorLogin);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ================= READ WITH FILTERS =================
    @GetMapping
    public ResponseEntity<List<DoctorLogin>> getAllDoctorLogins(

            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status
    ) {

        List<DoctorLogin> doctorLogins =
                doctorLoginRepository.findAll();

        List<DoctorLogin> filtered = doctorLogins.stream()

                // doctorId (exact)
                .filter(d -> doctorId == null ||
                        (d.getDoctorId() != null &&
                         d.getDoctorId().equalsIgnoreCase(doctorId)))

                // username (partial)
                .filter(d -> username == null ||
                        (d.getUsername() != null &&
                         d.getUsername().toLowerCase()
                          .contains(username.toLowerCase())))

                // email (exact)
                .filter(d -> email == null ||
                        (d.getEmail() != null &&
                         d.getEmail().equalsIgnoreCase(email)))

                // status
                .filter(d -> status == null ||
                        (d.getStatus() != null &&
                         d.getStatus().equalsIgnoreCase(status)))

                .collect(Collectors.toList());

        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }

    // ================= READ BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<DoctorLogin> getDoctorLoginById(
            @PathVariable String id) {

        Optional<DoctorLogin> doctorLogin =
                doctorLoginRepository.findById(id);

        return doctorLogin.map(value ->
                new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<DoctorLogin> updateDoctorLogin(
            @PathVariable String id,
            @RequestBody DoctorLogin doctorLogin
    ) {

        Optional<DoctorLogin> optional =
                doctorLoginRepository.findById(id);

        if (optional.isPresent()) {
            DoctorLogin existing = optional.get();

            existing.setUsername(doctorLogin.getUsername());
            existing.setEmail(doctorLogin.getEmail());
            existing.setStatus(doctorLogin.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());

            DoctorLogin updated =
                    doctorLoginRepository.save(existing);

            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDoctorLogin(
            @PathVariable String id) {

        try {
            doctorLoginRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}