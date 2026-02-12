package com.example.springcrud.controller;

import java.util.List;
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

import com.example.springcrud.model.Doctor;
import com.example.springcrud.repository.DoctorRepository;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*") // Allows your frontend to communicate with this backend
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        try {
            Doctor savedDoctor = doctorRepository.save(doctor);
            return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================
    // READ ALL
    // =========================
    @GetMapping
    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    // =========================
    // READ BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable String id) {
        return doctorRepository.findById(id)
                .map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // =========================
    // UPDATE (Handles both Full and Partial Updates)
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable String id, @RequestBody Doctor newData) {
        return doctorRepository.findById(id).map(existingDoctor -> {
            // Only update fields if they are provided in the RequestBody (Prevents nulling out data)
            if (newData.getName() != null) existingDoctor.setName(newData.getName());
            if (newData.getSpecialization() != null) existingDoctor.setSpecialization(newData.getSpecialization());
            if (newData.getExperience() != null) existingDoctor.setExperience(newData.getExperience());
            if (newData.getQualification() != null) existingDoctor.setQualification(newData.getQualification());
            if (newData.getGender() != null) existingDoctor.setGender(newData.getGender());
            if (newData.getPhone() != null) existingDoctor.setPhone(newData.getPhone());

            Doctor updated = doctorRepository.save(existingDoctor);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!doctorRepository.existsById(id)) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        doctorRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // =====================================================
    // 🔍 ADVANCED FILTER API
    // =====================================================
    @GetMapping("/search")
    public List<Doctor> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String qualification,
            @RequestParam(required = false) Integer minExp,
            @RequestParam(required = false) Integer maxExp,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        List<Doctor> list = doctorRepository.findAll();

        // Filter by Name (Case-Insensitive)
        if (name != null && !name.isEmpty()) {
            list = list.stream()
                    .filter(d -> d.getName() != null && d.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by Specialization
        if (specialization != null && !specialization.isEmpty()) {
            list = list.stream()
                    .filter(d -> d.getSpecialization() != null && d.getSpecialization().equalsIgnoreCase(specialization))
                    .collect(Collectors.toList());
        }

        // Filter by Gender
        if (gender != null) {
            list = list.stream()
                    .filter(d -> d.getGender() != null && d.getGender().equalsIgnoreCase(gender))
                    .collect(Collectors.toList());
        }

        // Filter by Phone
        if (phone != null) {
            list = list.stream()
                    .filter(d -> d.getPhone() != null && d.getPhone().contains(phone))
                    .collect(Collectors.toList());
        }

        // Filter by Qualification List
        if (qualification != null) {
            list = list.stream()
                    .filter(d -> d.getQualification() != null && 
                            d.getQualification().stream().anyMatch(q -> q.equalsIgnoreCase(qualification)))
                    .collect(Collectors.toList());
        }

        // Range Filter: Experience
        if (minExp != null) {
            list = list.stream().filter(d -> d.getExperience() != null && d.getExperience() >= minExp).collect(Collectors.toList());
        }
        if (maxExp != null) {
            list = list.stream().filter(d -> d.getExperience() != null && d.getExperience() <= maxExp).collect(Collectors.toList());
        }

        // Sorting Logic
        if (sortBy != null) {
            if (sortBy.equalsIgnoreCase("name")) {
                list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            } else if (sortBy.equalsIgnoreCase("experience")) {
                list.sort((a, b) -> Integer.compare(a.getExperience(), b.getExperience()));
            }
            
            if (order.equalsIgnoreCase("desc")) {
                java.util.Collections.reverse(list);
            }
        }

        return list;
    }

    // =========================
    // DASHBOARD METRICS
    // =========================
    @GetMapping("/count")
    public long getCount() {
        return doctorRepository.count();
    }

    // =========================
    // VALIDATION: EXISTS BY PHONE
    // =========================
    @GetMapping("/exists/phone")
    public boolean existsByPhone(@RequestParam String phone) {
        // Optimization: Use a repository custom method like existsByPhone(phone) 
        // if your DB supports it, otherwise this stream works for small datasets.
        return doctorRepository.findAll().stream()
                .anyMatch(d -> d.getPhone() != null && d.getPhone().equals(phone));
    }
}