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

import com.example.springcrud.model.MedicalTest;
import com.example.springcrud.repository.MedicalTestRepository;

@RestController
@RequestMapping("/api/medical-tests")
@CrossOrigin(origins = "*") // Allows connection from Port 5500
public class MedicalTestController {

    @Autowired
    private MedicalTestRepository medicalTestRepository;

    // READ - Get all medical tests
    @GetMapping
    public ResponseEntity<List<MedicalTest>> getAllMedicalTests() {
        return new ResponseEntity<>(medicalTestRepository.findAll(), HttpStatus.OK);
    }

    // ✅ ADDED: COUNT - For Dashboard Stat Cards
    // This allows dashboard.js to fetch a simple number instead of a full list
    @GetMapping("/count")
    public long getCount() {
        return medicalTestRepository.count();
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicalTest> getMedicalTestById(@PathVariable String id) {
        return medicalTestRepository.findById(id)
                .map(test -> new ResponseEntity<>(test, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * FILTER - Multi-criteria filter for Medical Tests
     */
    @GetMapping("/filter")
    public ResponseEntity<List<MedicalTest>> filterMedicalTests(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String resultStatus) {
        
        List<MedicalTest> tests = medicalTestRepository.findAll();

        if (patientId != null && !patientId.isEmpty()) {
            tests = tests.stream()
                .filter(t -> t.getPatientId() != null && t.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
        }

        if (doctorId != null && !doctorId.isEmpty()) {
            tests = tests.stream()
                .filter(t -> t.getDoctorId() != null && t.getDoctorId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
        }

        if (testName != null && !testName.isEmpty()) {
            tests = tests.stream()
                .filter(t -> t.getTestName() != null && 
                             t.getTestName().toLowerCase().contains(testName.toLowerCase()))
                .collect(Collectors.toList());
        }

        if (category != null && !category.isEmpty()) {
            tests = tests.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        }

        if (resultStatus != null && !resultStatus.isEmpty()) {
            tests = tests.stream()
                .filter(t -> t.getResultStatus() != null && t.getResultStatus().equalsIgnoreCase(resultStatus))
                .collect(Collectors.toList());
        }

        return new ResponseEntity<>(tests, HttpStatus.OK);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<MedicalTest> createMedicalTest(@RequestBody MedicalTest medicalTest) {
        try {
            // Business Logic: Ensure default status
            if (medicalTest.getResultStatus() == null) medicalTest.setResultStatus("Pending");
            
            MedicalTest savedTest = medicalTestRepository.save(medicalTest);
            return new ResponseEntity<>(savedTest, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MedicalTest> updateMedicalTest(@PathVariable String id, @RequestBody MedicalTest testDetails) {
        return medicalTestRepository.findById(id)
            .map(test -> {
                test.setPatientId(testDetails.getPatientId());
                test.setDoctorId(testDetails.getDoctorId());
                test.setTestName(testDetails.getTestName());
                test.setCategory(testDetails.getCategory());
                test.setPrice(testDetails.getPrice());
                test.setDescription(testDetails.getDescription());
                test.setResultStatus(testDetails.getResultStatus());
                test.setHistory(testDetails.getHistory());
                return new ResponseEntity<>(medicalTestRepository.save(test), HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalTest(@PathVariable String id) {
        try {
            if (medicalTestRepository.existsById(id)) {
                medicalTestRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}